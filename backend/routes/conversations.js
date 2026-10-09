const express = require('express');
const router = express.Router();
const { protect } = require('./auth');
const Conversation = require('../models/Conversation');

// @route   GET /api/conversations
router.get('/', protect, async (req, res) => {
  try {
    const conversations = await Conversation.find({
      participants: { $in: [req.user._id] }
    })
      .populate('participants', 'name email avatar isOnline lastSeen statusBio')
      .populate('lastMessage')
      .sort({ updatedAt: -1 });

    res.status(200).json({ success: true, count: conversations.length, conversations });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
});

// @route   POST /api/conversations (Direct Chat or Group)
router.post('/', protect, async (req, res) => {
  try {
    const { isGroup, name, participantIds, description } = req.body;

    if (!isGroup) {
      const recipientId = participantIds[0];
      let existing = await Conversation.findOne({
        isGroup: false,
        participants: { $all: [req.user._id, recipientId], $size: 2 }
      }).populate('participants', 'name email avatar isOnline lastSeen statusBio');

      if (existing) {
        return res.status(200).json({ success: true, conversation: existing });
      }
    }

    const allParticipants = [...new Set([...participantIds, req.user._id.toString()])];

    const conversation = await Conversation.create({
      isGroup: isGroup || false,
      name: isGroup ? name : '',
      description: description || '',
      participants: allParticipants,
      admin: isGroup ? req.user._id : null
    });

    const populated = await Conversation.findById(conversation._id).populate('participants', 'name email avatar isOnline lastSeen statusBio');
    res.status(201).json({ success: true, conversation: populated });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
});

module.exports = router;
