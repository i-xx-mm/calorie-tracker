# Calorie Tracker Frontend

A modern Angular 18 web application for tracking daily calorie intake with automatic food calorie lookup from public APIs, user authentication, and BMI management.

## Tech Stack

- **Angular 18** - Frontend framework
- **TypeScript 5.5** - Programming language
- **RxJS 7.8** - Reactive programming
- **CSS 3** - Styling
- **HTML 5** - Markup
- **Open Food Facts API** - Free nutrition database (no authentication required)

## Prerequisites

- Node.js 18+ and npm
- Angular CLI 18+
- Spring Boot backend running on `http://localhost:8080` (optional - can use mock server)

## Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd calorie-tracker
```

### 2. Install Dependencies
```bash
npm install
```

### 3. Start Development Server
```bash
npm start
```

The application opens at `http://localhost:4200`

### 4. (Optional) Start Mock Backend

If you don't have the Java backend running:
```bash
node mock-server.js
```

The mock server runs on `http://localhost:8080` and provides:
- User authentication (JWT)
- Food database with realistic calorie data
- Food logging & dashboard endpoints

## Available Commands

```bash
# Start development server
npm start
# or
ng serve

# Build for production
npm run build

# Run unit tests
npm test

# Run linter
npm run lint

# Start mock backend (for testing without Java backend)
node mock-server.js
```

## Environment Configuration

Update `src/environments/environment.ts` to configure the backend URL:

```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api',
  jwtTokenKey: 'calorie_jwt_token'
};
```

## Project Structure

```
src/
├── app/
│   ├── app.component.ts
│   ├── app.module.ts              # Root module
│   ├── app-routing.module.ts      # Routing with lazy loading
│   ├── features/                  # Feature modules
│   │   ├── auth/                  # Login & registration
│   │   ├── dashboard/             # Daily & monthly stats
│   │   ├── profile/               # User profile
│   │   └── food-log/              # Food search & logging
│   └── shared/                    # Shared module
│       ├── components/
│       ├── services/
│       │   ├── api.service.ts     # HTTP requests
│       │   ├── food.service.ts    # Food search & logging
│       │   ├── auth.service.ts    # Authentication
│       │   └── notification.service.ts
│       ├── models/
│       ├── guards/                # Route protection
│       └── interceptors/          # JWT & error handling
├── environments/                  # Environment configs
├── main.ts                        # Bootstrap entry
└── styles.css                     # Global styles
```

## How Food Calorie Lookup Works

### Search Flow (Priority Order)

1. **Try Backend API** (`/api/foods?search=...`)
   - If backend has cached data, use it immediately ✓
   
2. **If Backend Returns Empty**
   - Fall back to **Open Food Facts API** (free, no auth)
   - URL: `https://world.openfoodfacts.org/cgi/search.pl`
   - Returns: 3M+ food products with nutrition data
   
3. **If Backend Completely Down**
   - Still falls back to Open Food Facts API
   - App works even without Java backend ✓

### Real-World Data
- Open Food Facts has **beef, chicken, pizza**, etc.
- 3 million+ real food products
- Community-maintained database (like Wikipedia for food)
- Accurate nutrition data per 100g

### Mock Server Role
- **NOT** for food searching (uses real Open Food Facts)
- **FOR**: Auth, food logging, dashboard calculations
- Useful when testing without running Java backend

## Running Frontend + Backend Together

### Terminal 1 - Start Backend
```bash
cd ../calorie
MONGODB_URI="mongodb://localhost:27017/calorie_tracker" \
JWT_SECRET="your-super-secret-jwt-key-change-this-in-production-min-32-chars" \
JWT_EXPIRATION="86400000" \
mvn spring-boot:run
```

Backend runs on `http://localhost:8080/api`

### Terminal 2 - Start Frontend
```bash
cd calorie-tracker
npm start
```

Frontend runs on `http://localhost:4200`

## Features

**User authentication** with JWT tokens  
**Daily food logging** with instant calorie calculation  
**Personalized calorie goals** calculated using Mifflin-St Jeor equation (based on age, gender, weight, height)  
**Dynamic BMI calculation** based on height/weight with status (Underweight/Normal/Overweight/Obese)  
**Monthly statistics** with charts (only shows if data exists)  
**Responsive design** for all devices  
**Error handling** with user notifications  
**Protected routes** with auth guards  
**Fallback to mock server** for testing without backend  
**User avatar dropdown** menu (top right) with profile, avatar color picker, & logout  

## Architecture

Uses traditional **NgModule architecture** (not standalone):

- **AppModule**: Root module with shared services
- **Feature Modules**: Auth, Dashboard, Profile, Food-Log (lazy loaded)
- **SharedModule**: Shared components, services, guards, interceptors
- **Header Component**: Navigation bar with user avatar dropdown
- **Interceptors**: JWT attachment, error handling
- **Guards**: Route protection for authenticated pages

## User Interface

### Navigation & User Menu
- **Left side**: App logo
- **Center**: Navigation links (Dashboard, Profile, Food Log)
- **Right side**: User avatar circle with dropdown menu
  - Avatar shows first letter of username
  - Avatar color is customizable
  - Click avatar to toggle dropdown with smooth animation
  - Dropdown has a **pointer arrow** pointing to avatar
  - Shows username (read-only)
  - **User Profile** link (bold text)
  - **Avatar Color Picker** - 8 colors to choose from (saved to browser)
  - **Log Out** button (bold text)
  - Click outside to close dropdown

## Calorie Goals & Calculation

### Personalized Daily Calorie Target

The app calculates your daily calorie goal using the **Mifflin-St Jeor Equation** (most accurate modern formula):

```
For Men:    BMR = 10(weight) + 6.25(height) - 5(age) + 5
For Women:  BMR = 10(weight) + 6.25(height) - 5(age) - 161
```

Then multiplies by 1.55 (moderate activity level) to get your Total Daily Energy Expenditure (TDEE).

**Example:**
- Male, 30 years, 80kg, 180cm
- BMR = 10(80) + 6.25(180) - 5(30) + 5 = 1,705 kcal
- TDEE = 1,705 × 1.55 = **2,643 kcal/day**

### Factors Used
- **Age**: Affects metabolic rate (older = lower)
- **Gender**: Men typically have higher baseline metabolism
- **Weight**: More weight = higher energy needs
- **Height**: Affects surface area and metabolism

### BMI Status
Based on calculated BMI:
- **Underweight**: BMI < 18.5
- **Normal**: BMI 18.5-24.9 ✓
- **Overweight**: BMI 25-29.9
- **Obese**: BMI ≥ 30

### Monthly Summary
- Only displays when you have logged food entries for the month
- Shows trends and patterns
- Empty on new accounts or fresh months

### Backend API (if available)
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/foods?search=...` - Search foods
- `POST /api/foodlogs` - Add food entry
- `GET /api/foodlogs?date=...` - Get day's food log
- `GET /api/dashboard/today` - Today's stats
- `GET /api/dashboard/monthly-stats` - Monthly stats

### Open Food Facts API (fallback)
- No authentication required
- Automatic fallback when backend unavailable
- Provides global food database with nutrition data

## Troubleshooting

**Cannot connect to backend**
- Ensure Spring Boot is running on port 8080
- Or start mock server: `node mock-server.js`
- Check `src/environments/environment.ts` has correct URL

**Port 4200 already in use**
```bash
ng serve --port 4201
```

**No calorie data appearing**
- Check network tab for API calls
- Food Facts API may have rate limits (10 req/min)
- Mock server has built-in food database

**Build errors**
```bash
rm -rf node_modules package-lock.json
npm install
ng build --configuration development
```

## Future Enhancements

### Potential Health Advice APIs
- **Nutritionix API** - Detailed nutrition info for foods
- **USDA FoodData Central** - Comprehensive food database
- **edamam API** - Recipe & nutrition analysis
- **Unsplash API** - Food images for recipes
- **spoonacular API** - Meal planning & recipes
- **Health tips service** - Personalized wellness recommendations

### Possible Features to Add
- 📱 Mobile app (React Native/Flutter)
- 📊 Export reports (PDF, CSV)
- 🎯 Meal planning assistance
- 💬 Notifications/reminders
- 🏆 Achievement badges
- 👥 Social sharing
- 📈 Trend analysis & predictions
- 🤖 AI-powered meal suggestions

```bash
npm run build
# Output: dist/calorie-tracker/
```

Configure backend URL in `src/environments/environment.prod.ts` before deploying.

## Development Notes

- Changes auto-reload in development mode
- Mock server included for testing without Java backend
- Food database has realistic calories for common foods
- All API calls have error handling and fallbacks

---

**Frontend Version**: 1.0.0  
**Angular Version**: 18.0.0  
**Node Version**: 18+  
**API Integration**: Open Food Facts (free, no auth)  
