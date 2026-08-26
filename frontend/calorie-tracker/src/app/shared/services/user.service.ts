import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { User } from '../models/user.model';

/**
 * User service handles user profile API operations
 */
@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private apiService: ApiService) {}

  /**
   * Fetch currently authenticated user's own profile
   *
   * @returns Observable of current User profile
   */
  getCurrentUser(): Observable<User> {
    return this.apiService.get<User>('/users/me');
  }

  /**
   * Fetch public user profile by username
   *
   * @param username target user username
   * @returns Observable of target User profile
   */
  getProfile(username: string): Observable<User> {
    return this.apiService.get<User>(`/users/${username}`);
  }

  /**
   * Update user profile with partial payload
   *
   * @param username target user username
   * @param profile partial User object containing fields to update
   * @returns Observable updated User profile from backend
   */
  updateProfile(username: string, profile: Partial<User>): Observable<User> {
    return this.apiService.put<User>(`/users/${username}`, profile);
  }
}
