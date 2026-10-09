# MyChat — WhatsApp-Style Messaging App & Full-Stack Platform

MyChat is a real-time messaging application inspired by WhatsApp, featuring original emerald green and teal branding, native Android client built with Jetpack Compose, and a complete Node.js, Express, Socket.IO, and MongoDB backend.

---

## 📱 Features

### 1. WhatsApp-Inspired Interface & Themes
- **Emerald & Teal Theme**: `#128C7E`, `#075E54`, `#00A884`, `#25D366`.
- **Light & Dark Mode**: Authentic WhatsApp color scheme with `#121B22` / `#1F2C34` dark surface tones and `#005C4B` dark outgoing bubbles.
- **Chat Wallpapers**: Classic doodle, Emerald, Midnight, and Clean Sand.

### 2. Real-Time Chat & Delivery Indicators
- **Delivery Status Checkmarks**:
  - ⏱️ `SENDING`: Clock indicator
  - ✔️ `SENT`: Single gray checkmark
  - ✔️✔️ `DELIVERED`: Double gray checkmark
  - 🟦 `READ`: Double sky-blue checkmark (`#53BDEB`)
- **Live Online & Typing Alerts**: "online", "typing...", and "last seen" timestamps.
- **Audio Voice Notes**:
  - Live recording with waveform visualization and timer (`0:01`, `0:02`...).
  - Playback controls with interactive waveform bars.
- **Media & Documents**: Photo previews, documents (`.pdf`), location, and contact cards.
- **Message Interactions**: Replying to messages with quote preview, starring, copying, and deleting ("This message was deleted").

### 3. Navigation & Dashboard
- **Tabs**: `CHATS` (with unread counter badges), `GROUPS`, `STATUS` (stories), and `CALLS` (voice/video logs).
- **Search**: Instant search across conversations, contacts, and in-chat messages.
- **Group Management**: Create groups, add participants, view group info, and manage members.
- **Settings & Privacy**: Last seen privacy (Everyone / Contacts / Nobody), Read Receipts toggle, Screen Lock, notification tones, and vibration.

---

## 💻 Full-Stack Architecture

```
├── app/                        # Android Jetpack Compose Native Client
│   ├── src/main/java/com/example/
│   │   ├── MainActivity.kt     # App entry & screen routing
│   │   ├── model/Models.kt     # Type-safe Kotlin models
│   │   ├── data/SampleData.kt  # Seed conversations and messages
│   │   ├── viewmodel/          # ChatViewModel with reactive StateFlow
│   │   ├── ui/components/      # MessageBubble, ChatInputBar, AvatarView, StatusTick
│   │   └── ui/screens/         # Landing, Auth, Dashboard, Chat, Contacts, Settings
│   └── res/                    # Adaptive launcher icons, drawables, strings
└── backend/                    # Node.js + Express + Socket.IO + MongoDB Server
    ├── server.js               # Express server and Socket.IO initialization
    ├── package.json            # Node.js dependencies
    ├── .env.example            # Environment configuration
    ├── models/                 # Mongoose schemas (User, Message, Conversation)
    ├── routes/                 # REST API endpoints (auth, messages, conversations)
    ├── socket/                 # Real-time WebSocket event handlers
    └── seed/                   # Sample development seed data
```

---

## 🚀 Step-by-Step Setup & Windows Local Run

### Prerequisites
- [Node.js](https://nodejs.org/) v18+ installed on your system.
- [MongoDB](https://www.mongodb.com/try/download/community) running locally or a [MongoDB Atlas](https://www.mongodb.com/atlas) connection URI.
- [Android Studio](https://developer.android.com/studio) or Android Emulator for the mobile app.

### 1. Running the Backend on Windows (PowerShell or CMD)

```powershell
# 1. Navigate to the backend directory
cd backend

# 2. Install dependencies
npm install

# 3. Create your local .env file from .env.example
copy .env.example .env

# 4. Seed development data (creates test accounts and sample messages)
npm run seed

# 5. Start the backend development server
npm run dev
```

The server will be available at:
`http://localhost:5000`
- API Health Check: `http://localhost:5000/api/health`
- WebSocket server: `ws://localhost:5000`

### 2. Running the Android Application

1. Open this project in **Android Studio**.
2. Select your Android Device or start an Android Emulator.
3. Click **Run** (`Shift + F10`) or execute in terminal:
```powershell
gradle :app:installDebug
```

---

## 🌐 Deployment Instructions

### Backend Deployment (Render / Railway / Heroku)
1. Push your repository to GitHub.
2. In your cloud provider dashboard (e.g. Render.com or Railway.app):
   - Set Build Command: `cd backend && npm install`
   - Set Start Command: `cd backend && node server.js`
3. Add Environment Variables:
   - `PORT`: `5000`
   - `MONGODB_URI`: `mongodb+srv://<user>:<password>@cluster0.mongodb.net/mychat`
   - `JWT_SECRET`: A secure random 64-character string
   - `CLIENT_URL`: Your deployed frontend URL

### Frontend / Web Deployment (Vercel)
- Set root directory to your web frontend client.
- Set `REACT_APP_API_URL` or `NEXT_PUBLIC_API_URL` to your backend URL (e.g. `https://mychat-backend.onrender.com`).
