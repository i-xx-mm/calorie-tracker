export interface User {
  username: string;
  height: number;
  currentWeight: number;
  age: number;
  gender: 'male' | 'female' | 'other';
  goalCalories?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  expiresIn: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  height: number;
  weight: number;
  age: number;
  gender: 'male' | 'female' | 'other';
}