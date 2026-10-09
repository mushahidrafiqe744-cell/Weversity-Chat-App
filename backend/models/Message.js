const mongoose = require('mongoose');

const MessageSchema = new mongoose.Schema(
  {
    conversationId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Conversation',
      required: true,
      index: true
    },
    sender: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true
    },
    senderName: {
      type: String,
      required: true
    },
    text: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      enum: ['TEXT', 'IMAGE', 'AUDIO', 'DOCUMENT'],
      default: 'TEXT'
    },
    mediaUrl: {
      type: String,
      default: null
    },
    mediaCaption: {
      type: String,
      default: null
    },
    audioDurationSec: {
      type: Number,
      default: 0
    },
    documentName: {
      type: String,
      default: null
    },
    documentSize: {
      type: String,
      default: null
    },
    status: {
      type: String,
      enum: ['SENDING', 'SENT', 'DELIVERED', 'READ'],
      default: 'SENT'
    },
    replyTo: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Message',
      default: null
    },
    isDeleted: {
      type: Boolean,
      default: false
    }
  },
  { timestamps: true }
);

module.exports = mongoose.model('Message', MessageSchema);
