const Message = require('../models/Message');
const User = require('../models/User');

const initSocketIO = (io) => {
  const onlineUsers = new Map(); // userId -> socketId

  io.on('connection', (socket) => {
    console.log(`Socket client connected: ${socket.id}`);

    // User online registration
    socket.on('user_online', async (userId) => {
      onlineUsers.set(userId, socket.id);
      await User.findByIdAndUpdate(userId, { isOnline: true });
      io.emit('user_status_changed', { userId, isOnline: true });
    });

    // Join conversation room
    socket.on('join_conversation', (conversationId) => {
      socket.join(conversationId);
      console.log(`Socket ${socket.id} joined conversation room ${conversationId}`);
    });

    // Leave conversation room
    socket.on('leave_conversation', (conversationId) => {
      socket.leave(conversationId);
    });

    // Send Real-Time Message event
    socket.on('send_message', async (data) => {
      // Broadcast to room
      socket.to(data.conversationId).emit('receive_message', data);
    });

    // Typing Indicator
    socket.on('typing_start', ({ conversationId, userId, userName }) => {
      socket.to(conversationId).emit('user_typing', { conversationId, userId, userName });
    });

    socket.on('typing_stop', ({ conversationId, userId }) => {
      socket.to(conversationId).emit('user_stopped_typing', { conversationId, userId });
    });

    // Message Delivered & Read status updates
    socket.on('message_read', async ({ messageId, conversationId }) => {
      await Message.findByIdAndUpdate(messageId, { status: 'READ' });
      socket.to(conversationId).emit('message_status_updated', { messageId, status: 'READ' });
    });

    // Disconnect handler
    socket.on('disconnect', async () => {
      console.log(`Socket client disconnected: ${socket.id}`);
      let disconnectedUserId = null;
      for (const [userId, sockId] of onlineUsers.entries()) {
        if (sockId === socket.id) {
          disconnectedUserId = userId;
          onlineUsers.delete(userId);
          break;
        }
      }
      if (disconnectedUserId) {
        await User.findByIdAndUpdate(disconnectedUserId, { isOnline: false, lastSeen: new Date() });
        io.emit('user_status_changed', { userId: disconnectedUserId, isOnline: false, lastSeen: new Date() });
      }
    });
  });
};

module.exports = initSocketIO;
