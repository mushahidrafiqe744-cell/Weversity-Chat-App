const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
require('dotenv').config();

const User = require('../models/User');
const Conversation = require('../models/Conversation');
const Message = require('../models/Message');

const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017/mychat';

const sampleUsers = [
  {
    name: 'Alex Morgan',
    email: 'alex.morgan@mychat.io',
    phone: '+1 (555) 234-8901',
    password: 'password123',
    statusBio: 'Building real-time apps with Jetpack Compose 🚀'
  },
  {
    name: 'Sarah Jenkins',
    email: 'sarah.j@example.com',
    phone: '+1 (555) 301-4920',
    password: 'password123',
    statusBio: 'Design lead @ Studio • Coffee lover ☕'
  },
  {
    name: 'David Chen',
    email: 'david.c@example.com',
    phone: '+1 (555) 782-9912',
    password: 'password123',
    statusBio: 'Full-stack engineer & open-source tinkerer'
  }
];

const seedDB = async () => {
  try {
    await mongoose.connect(MONGODB_URI);
    console.log('Connected to MongoDB. Clearing existing data...');

    await User.deleteMany({});
    await Conversation.deleteMany({});
    await Message.deleteMany({});

    console.log('Seeding users...');
    const createdUsers = [];
    for (const u of sampleUsers) {
      const user = await User.create(u);
      createdUsers.push(user);
    }

    console.log('Creating sample direct conversation...');
    const conv = await Conversation.create({
      isGroup: false,
      participants: [createdUsers[0]._id, createdUsers[1]._id]
    });

    console.log('Seeding initial messages...');
    const m1 = await Message.create({
      conversationId: conv._id,
      sender: createdUsers[1]._id,
      senderName: createdUsers[1].name,
      text: 'Hey Alex! Did you get a chance to review the WhatsApp-style design tokens?',
      status: 'READ'
    });

    const m2 = await Message.create({
      conversationId: conv._id,
      sender: createdUsers[0]._id,
      senderName: createdUsers[0].name,
      text: 'Yes! The emerald green `#00A884` and dark surface tones feel authentic.',
      status: 'READ'
    });

    conv.lastMessage = m2._id;
    conv.lastMessageText = m2.text;
    await conv.save();

    console.log('Database seeded successfully! 🎉');
    process.exit(0);
  } catch (err) {
    console.error('Seed error:', err);
    process.exit(1);
  }
};

seedDB();
