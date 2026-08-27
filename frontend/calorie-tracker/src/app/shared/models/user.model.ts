/**
 * User profile entity holding physical mEtrics and calorie goal
 * Matches backend User document model
 */
export interface User {
  /** Username of the User */
  username: string;
  /** User height in cm */
  height: number;
  /** User current weight in kg */
  currentWeight: number;
  /** User age in full years */
  age: number;
  /** User gender */
  gender: 'male' | 'female' | 'other';
  /** Server-computed daily calorie target goal */
  goalCalories?: number;
  /** Account creation timestamp ISO string */
  createdAt?: string;
  /** Last profile update timestamp ISO string */
  updatedAt?: string;
}

/**
 * Current logged-in user
 */
export interface CurrentUser {
  username: string;
}

/**
 * Authentication API response after login/register success
 * Contains JWT access token and expiry metadata
 */
export interface AuthResponse {
  /** JWT bearer access token string */
  token: string;
  /** Authenticated user username */
  username: string;
  /** Token validity duration in milliseconds */
  expiresIn: number;
}

/**
 * Request body payload for user login POST request
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * Request body payload for new user account registration POST request
 */
export interface RegisterRequest {
  username: string;
  password: string;
  height: number;
  weight: number;
  age: number;
  gender: 'male' | 'female' | 'other';
}

/**
 * Model mapping to backend BMIResponse DTO
 */
export interface BMIResponse {
  username: string;
  height: number;
  weight: number;
  bmi: number;
  category: string;
  calculatedAt: string;
}
