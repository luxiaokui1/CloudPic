# CloudPic - AI-Powered Collaborative Cloud Image Platform

A full-stack, enterprise-grade intelligent image management platform with AI-powered analysis, real-time collaborative editing, and multi-level access control.

**Live Demo**: _Coming soon_

---

## Features

### AI-Powered Image Intelligence (Claude AI)
- **Auto Tagging** - Automatically generates relevant tags for uploaded images using Claude's vision capabilities
- **Auto Description** - Generates natural language descriptions for images
- **Auto Classification** - Categorizes images into appropriate categories
- **Content Moderation** - Automatically detects and blocks inappropriate content on upload
- **AI Image Expansion** - Expands image boundaries using AI out-painting (Alibaba Cloud DashScope)
- **Manual AI Analysis** - Admin endpoint to trigger AI analysis on existing images

### Image Management
- **Upload & Storage** - Upload images via file or URL, stored in MinIO (S3-compatible object storage)
- **Search & Filter** - Search by keyword, category, tags, color, format, dimensions, date range
- **Image Details** - View metadata including dimensions, format, size, dominant color, aspect ratio
- **Batch Operations** - Batch upload, batch edit (tags, categories, naming rules)
- **Image Editing** - Crop, rotate, zoom with real-time collaborative editing via WebSocket
- **Web Scraping** - Batch import images from the web by keyword

### Space Management
- **Private Spaces** - Personal image libraries with quota management
- **Team Spaces** - Shared team image libraries with role-based access control
- **Space Analytics** - Visualize storage usage, category distribution, tag analysis, size distribution, upload trends, user contribution, and space ranking

### User & Permission System
- **User Authentication** - Register/login with session management (Redis-backed)
- **Role-Based Access Control (RBAC)** - Admin, Editor, Viewer roles via Sa-Token
- **Space Permissions** - Fine-grained permissions per space (upload, edit, delete, manage members)
- **Admin Dashboard** - User management, picture management, space management, review system
- **VIP System** - Premium membership with redemption codes

### Image Review System
- **Review Workflow** - Pending → Approved / Rejected pipeline
- **Admin Review** - Admins can approve or reject uploaded images with review messages
- **Auto-Approval** - Admin uploads bypass review; AI moderation pre-filters unsafe content

---

## Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| **Java 11** | Runtime |
| **Spring Boot 2.7.6** | Web framework |
| **MySQL 8.0** | Primary database |
| **MyBatis Plus 3.5.9** | ORM with pagination, logical delete |
| **Redis 7** | Distributed cache, session storage |
| **Caffeine 3.1.8** | Local in-memory cache (multi-level caching) |
| **MinIO** | S3-compatible object storage (image files) |
| **Claude AI API** | Image analysis, tagging, moderation (Anthropic) |
| **Alibaba Cloud DashScope** | AI image out-painting |
| **Sa-Token 1.39.0** | Authentication & RBAC permission control |
| **WebSocket** | Real-time collaborative image editing |
| **ShardingSphere 5.2.0** | Database sharding (picture table by spaceId) |
| **Disruptor 3.4.2** | High-performance lock-free event queue |
| **Jsoup 1.15.3** | Web scraping for batch image import |
| **Knife4j 4.4.0** | OpenAPI documentation (Swagger) |
| **Hutool 5.8.26** | Java utility library |

### Frontend
| Technology | Purpose |
|---|---|
| **Vue 3.5** | UI framework |
| **TypeScript 5.6** | Type safety |
| **Vite 6** | Build tool & dev server |
| **Ant Design Vue 4.2** | UI component library |
| **Pinia 2.2** | State management |
| **Axios 1.7** | HTTP client |
| **ECharts 5.5** | Data visualization (analytics) |
| **Vue Cropper 1.1** | Image cropping |
| **Vue3 Colorpicker** | Color-based image search |
| **ESLint + Prettier** | Code quality |
| **OpenAPI Codegen** | Auto-generated API client |

### Infrastructure
| Technology | Purpose |
|---|---|
| **Docker & Docker Compose** | Container orchestration |
| **MinIO** | Self-hosted S3-compatible object storage |
| **MySQL 8.0** | Containerized database |
| **Redis 7** | Containerized cache |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (Vue 3)                      │
│   Ant Design Vue │ Pinia │ ECharts │ WebSocket Client        │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP / WebSocket
┌─────────────────────▼───────────────────────────────────────┐
│                     Backend (Spring Boot)                     │
│                                                               │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────────┐  │
│  │  Controller  │  │   Service    │  │     Manager         │  │
│  │  (REST API)  │──│  (Business)  │──│  CosManager(MinIO)  │  │
│  │             │  │              │  │  ClaudeAiManager    │  │
│  └─────────────┘  └──────────────┘  │  FileManager        │  │
│                                      └────────────────────┘  │
│  ┌──────────────────────────────────────────────────────┐    │
│  │  Infrastructure                                       │    │
│  │  Sa-Token │ Redis Cache │ Caffeine │ ShardingSphere    │    │
│  │  WebSocket │ Disruptor │ Jsoup                        │    │
│  └──────────────────────────────────────────────────────┘    │
└──────┬──────────┬──────────┬──────────┬──────────────────────┘
       │          │          │          │
  ┌────▼───┐ ┌───▼───┐ ┌───▼───┐ ┌───▼────────┐
  │ MySQL  │ │ Redis │ │ MinIO │ │ Claude API │
  │  8.0   │ │   7   │ │       │ │ (Anthropic)│
  └────────┘ └───────┘ └───────┘ └────────────┘
```

---

## Getting Started

### Prerequisites
- Docker & Docker Compose installed
- (Optional) Claude API key for AI features
- (Optional) Alibaba Cloud DashScope API key for AI image expansion

### Quick Start with Docker

```bash
# 1. Clone the repository
git clone <your-repo-url>
cd yu-picture

# 2. Start all services
docker-compose up -d --build

# 3. Wait ~30 seconds for services to start, then access:
#    Frontend:  http://localhost:5173
#    API Docs:  http://localhost:8123/api/doc.html
#    MinIO UI:  http://localhost:9001 (admin/minioadmin)
```

### API Configuration

All API keys are configured via environment variables in `docker-compose.yml`:

| Variable | Service | Required | Description |
|---|---|---|---|
| `CLAUDE_APIKEY` | Anthropic Claude | Optional | Enables AI image analysis, tagging, and moderation |
| `ALIYUNAI_APIKEY` | Alibaba DashScope | Optional | Enables AI image out-painting |
| `COS_CLIENT_ENDPOINT` | MinIO | Pre-configured | Object storage endpoint |
| `COS_CLIENT_SECRETID` | MinIO | Pre-configured | Storage access key |
| `COS_CLIENT_SECRETKEY` | MinIO | Pre-configured | Storage secret key |
| `SPRING_DATASOURCE_URL` | MySQL | Pre-configured | Database connection |
| `SPRING_REDIS_HOST` | Redis | Pre-configured | Cache connection |

#### Setting up Claude AI (Recommended)

1. Get an API key from [Anthropic Console](https://console.anthropic.com/)
2. Edit `docker-compose.yml`, change the `CLAUDE_APIKEY` value:
   ```yaml
   CLAUDE_APIKEY: sk-ant-your-actual-api-key
   ```
3. Restart the backend:
   ```bash
   docker-compose up -d backend
   ```

With Claude AI enabled:
- Uploaded images are automatically analyzed for tags, description, and category
- Inappropriate content is automatically blocked
- Admins can trigger manual AI analysis on any image via `POST /api/picture/ai/analyze`

#### Setting up AI Image Expansion (Optional)

1. Get an API key from [Alibaba Cloud DashScope](https://dashscope.aliyuncs.com/)
2. Edit `docker-compose.yml`, change the `ALIYUNAI_APIKEY` value
3. Restart the backend

### Services & Ports

| Service | Port | Description |
|---|---|---|
| Frontend | `5173` | Vue 3 web application |
| Backend API | `8123` | Spring Boot REST API (`/api` context path) |
| MinIO API | `9000` | S3-compatible object storage |
| MinIO Console | `9001` | MinIO web management (user: `minioadmin`, pass: `minioadmin`) |
| MySQL | `3306` | Database (user: `root`, pass: `123456`) |
| Redis | `6379` | Cache & session store |

---

## API Documentation

Once running, visit **http://localhost:8123/api/doc.html** for the interactive Swagger API documentation.

### Key API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/user/register` | Register new user |
| `POST` | `/api/user/login` | User login |
| `POST` | `/api/picture/upload` | Upload image (with auto AI analysis) |
| `POST` | `/api/picture/ai/analyze` | Manually trigger AI analysis on a picture |
| `POST` | `/api/picture/list/page/vo` | Search pictures with pagination |
| `POST` | `/api/space/add` | Create a new space |
| `POST` | `/api/spaceUser/add` | Add member to team space |
| `WS` | `/api/ws/picture/edit` | WebSocket for collaborative editing |

---

## Database Schema

| Table | Description |
|---|---|
| `user` | User accounts, roles, VIP status |
| `picture` | Image metadata, tags, categories, review status |
| `space` | Private/team spaces with quotas |
| `space_user` | Space membership and roles |

---

## Project Structure

```
yu-picture/
├── docker-compose.yml              # Docker orchestration
├── yu-picture-backend/             # Spring Boot backend
│   ├── src/main/java/.../
│   │   ├── api/                    # External API clients
│   │   │   ├── aliyunai/           # Alibaba Cloud AI (out-painting)
│   │   │   └── claudeai/           # Claude AI (tagging, moderation)
│   │   ├── config/                 # Spring configurations
│   │   ├── controller/             # REST API endpoints
│   │   ├── manager/                # Business managers (MinIO, AI, files)
│   │   ├── model/                  # Entities, DTOs, VOs, enums
│   │   ├── service/                # Business logic layer
│   │   └── websocket/              # Real-time collaboration
│   └── sql/                        # Database initialization scripts
├── yu-picture-frontend/            # Vue 3 frontend
│   └── src/
│       ├── api/                    # Auto-generated API client
│       ├── components/             # Reusable UI components
│       │   └── analyze/            # Analytics chart components
│       ├── constants/              # App constants
│       ├── layouts/                # Page layouts
│       ├── pages/                  # Page views
│       │   └── admin/              # Admin dashboard pages
│       ├── stores/                 # Pinia state management
│       └── utils/                  # Utilities & WebSocket client
└── yu-picture-backend-ddd/         # DDD architecture version (reference)
```

---

## License

This project is for educational and portfolio purposes.
