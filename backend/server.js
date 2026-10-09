const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config();

const { router: authRoutes } = require('./routes/auth');
const messageRoutes = require('./routes/messages');
const conversationRoutes = require('./routes/conversations');
const initSocketIO = require('./socket/chatSocket');

const app = express();
const server = http.createServer(app);

// CORS configuration
const allowedOrigins = [
  process.env.CLIENT_URL || 'http://localhost:3000',
  'http://localhost:5173',
  'http://10.0.2.2:5000' // Android emulator loopback
];

app.use(
  cors({
    origin: (origin, callback) => {
      // Allow requests with no origin (e.g. mobile apps or curl)
      if (!origin || allowedOrigins.indexOf(origin) !== -1 || true) {
        callback(null, true);
      } else {
        callback(new Error('Not allowed by CORS'));
      }
    },
    credentials: true
  })
);

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Root route - Web Landing & API Status Page
app.get('/', (req, res) => {
  res.send(`
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>MyChat — Real-Time Messaging Server</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; }
    body { background-color: #0c1317; color: #e9edef; display: flex; flex-direction: column; min-height: 100vh; }
    header { background-color: #1f2c34; padding: 18px 24px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #2a3942; }
    .brand { display: flex; align-items: center; gap: 12px; }
    .logo-icon { width: 38px; height: 38px; background: #00a884; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 20px; }
    .title { font-size: 20px; font-weight: bold; color: #00a884; }
    .badge-live { background: rgba(37, 211, 102, 0.15); color: #25d366; border: 1px solid #25d366; padding: 4px 12px; border-radius: 20px; font-size: 13px; font-weight: 600; display: flex; align-items: center; gap: 6px; }
    .badge-dot { width: 8px; height: 8px; background: #25d366; border-radius: 50%; display: inline-block; animation: pulse 2s infinite; }
    @keyframes pulse { 0% { opacity: 1; } 50% { opacity: 0.3; } 100% { opacity: 1; } }
    main { flex: 1; max-width: 800px; margin: 0 auto; width: 100%; padding: 40px 20px; }
    .hero-card { background: #121b22; border: 1px solid #2a3942; border-radius: 16px; padding: 32px; margin-bottom: 24px; text-align: center; }
    .hero-card h1 { font-size: 28px; margin-bottom: 12px; color: #e9edef; }
    .hero-card p { color: #8696a0; font-size: 15px; line-height: 1.6; max-width: 600px; margin: 0 auto 24px; }
    .api-card { background: #121b22; border: 1px solid #2a3942; border-radius: 16px; padding: 24px; }
    .api-card h2 { font-size: 18px; margin-bottom: 16px; color: #00a884; }
    .endpoint-list { display: flex; flex-direction: column; gap: 10px; }
    .endpoint { display: flex; align-items: center; justify-content: space-between; background: #1f2c34; padding: 12px 16px; border-radius: 8px; text-decoration: none; color: inherit; transition: 0.2s; }
    .endpoint:hover { background: #2a3942; }
    .method { font-size: 11px; font-weight: bold; padding: 4px 8px; border-radius: 4px; }
    .get { background: #00a884; color: #fff; }
    .post { background: #1e88e5; color: #fff; }
    .path { font-family: monospace; font-size: 14px; margin-left: 10px; color: #e9edef; }
    .status { color: #8696a0; font-size: 13px; }
    footer { text-align: center; padding: 20px; color: #8696a0; font-size: 13px; border-top: 1px solid #2a3942; }
  </style>
</head>
<body>
  <header>
    <div class="brand">
      <div class="logo-icon">💬</div>
      <div class="title">MyChat</div>
    </div>
    <div class="badge-live"><span class="badge-dot"></span> Server Active</div>
  </header>

  <main>
    <div class="hero-card">
      <h1>WhatsApp-Style Real-Time Messaging API</h1>
      <p>The backend service is successfully running and accepting connections. Connect the Android client, web application, or explore the REST API endpoints below.</p>
      <a href="/api/health" style="display: inline-block; background: #00a884; color: white; text-decoration: none; padding: 12px 24px; border-radius: 24px; font-weight: 600;">Check Health Status</a>
    </div>

    <div class="api-card">
      <h2>Available REST API Endpoints</h2>
      <div class="endpoint-list">
        <a href="/api/health" class="endpoint">
          <div><span class="method get">GET</span><span class="path">/api/health</span></div>
          <span class="status">System Health & Timestamp ➔</span>
        </a>
        <div class="endpoint">
          <div><span class="method post">POST</span><span class="path">/api/auth/register</span></div>
          <span class="status">Register user account</span>
        </div>
        <div class="endpoint">
          <div><span class="method post">POST</span><span class="path">/api/auth/login</span></div>
          <span class="status">User authentication & JWT</span>
        </div>
        <div class="endpoint">
          <div><span class="method get">GET</span><span class="path">/api/conversations</span></div>
          <span class="status">Fetch user chats & groups</span>
        </div>
        <div class="endpoint">
          <div><span class="method post">POST</span><span class="path">/api/messages</span></div>
          <span class="status">Send message & voice notes</span>
        </div>
      </div>
    </div>
  </main>

  <footer>
    MyChat Engine • Node.js, Express, Socket.IO & Jetpack Compose
  </footer>
</body>
</html>
  `);
});

// API Routes
app.use('/api/auth', authRoutes);
app.use('/api/messages', messageRoutes);
app.use('/api/conversations', conversationRoutes);

app.get('/api/health', (req, res) => {
  res.status(200).json({ status: 'healthy', timestamp: new Date().toISOString() });
});

// Setup Socket.IO
const io = socketIo(server, {
  cors: {
    origin: '*',
    methods: ['GET', 'POST']
  }
});
initSocketIO(io);

// MongoDB connection with caching for serverless
const PORT = process.env.PORT || 5000;
const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017/mychat';

let isConnected = false;
const connectDB = async () => {
  if (isConnected) return;
  try {
    await mongoose.connect(MONGODB_URI, {
      serverSelectionTimeoutMS: 5000
    });
    isConnected = true;
    console.log('MongoDB connected successfully');
  } catch (err) {
    console.error('MongoDB connection error:', err.message);
  }
};

connectDB();

// Middleware to ensure DB connection on serverless requests
app.use(async (req, res, next) => {
  await connectDB();
  next();
});

// Run server.listen only when not running inside Vercel serverless environment
if (!process.env.VERCEL) {
  server.listen(PORT, () => {
    console.log(`MyChat Server running on port ${PORT}`);
  });
}

module.exports = app;
