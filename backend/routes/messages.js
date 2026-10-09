const express = require('express');
const router = express.Router();
const { protect } = require('./auth');
const Message = require('../models/Message');
const Conversation = require('../models/Conversation');

// @route   GET /api/messages/:conversationId
router.get('/:conversationId', protect, async (req, res) => {
  try {
    const messages = await Message.find({ conversationId: req.params.conversationId })
      .populate('sender', 'name avatar')
      .populate('replyTo', 'senderName text')
      .sort({ createdAt: 1 });

    res.status(200).json({ success: true, count: messages.length, messages });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
});

// @route   POST /api/messages
router.post('/', protect, async (req, res) => {
  try {
    const { conversationId, text, type, mediaUrl, mediaCaption, audioDurationSec, documentName, documentSize, replyTo } = req.body;

    const message = await Message.create({
      conversationId,
      sender: req.user._id,
      senderName: req.user.name,
      text: text || '',
      type: type || 'TEXT',
      mediaUrl,
      mediaCaption,
      audioDurationSec,
      documentName,
      documentSize,
      replyTo,
      status: 'SENT'
    });

    // Update conversation lastMessage
    const preview = type === 'AUDIO' ? `🎤 Voice message (${audioDurationSec}s)` : (type === 'IMAGE' ? '📷 Photo' : text);
    await Conversation.findByIdAndUpdate(conversationId, {
      lastMessage: message._id,
      lastMessageText: preview,
      lastMessageTime: Date.now()
    });

    res.status(201).json({ success: true, message });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
});

// @route   DELETE /api/messages/:id
router.delete('/:id', protect, async (req, res) => {
  try {
    const message = await Message.findById(req.params.id);
    if (!message) {
      return res.status(404).json({ success: false, message: 'Message not found' });
    }
    if (message.sender.toString() !== req.user._id.toString()) {
      return res.status(403).json({ success: false, message: 'Not authorized to delete this message' });
    }
    message.isDeleted = true;
    message.text = 'This message was deleted';
    await message.save();

    res.status(200).json({ success: true, message });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
});

module.exports = router;
