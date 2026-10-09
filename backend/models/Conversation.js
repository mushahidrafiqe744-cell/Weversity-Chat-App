const mongoose = require('mongoose');

const ConversationSchema = new mongoose.Schema(
  {
    isGroup: {
      type: Boolean,
      default: false
    },
    name: {
      type: String,
      trim: true
    },
    description: {
      type: String,
      default: ''
    },
    avatar: {
      type: String,
      default: ''
    },
    participants: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
      }
    ],
    admin: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    lastMessage: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Message'
    },
    lastMessageText: {
      type: String,
      default: ''
    },
    lastMessageTime: {
      type: Date,
      default: Date.now
    }
  },
  { timestamps: true }
);

module.exports = mongoose.model('Conversation', ConversationSchema);
