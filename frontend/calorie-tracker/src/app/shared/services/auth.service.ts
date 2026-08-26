import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ApiService } from './api.service';
import {
  AuthResponse,
  CurrentUser,
  LoginRequest,
  RegisterRequest,
} from '../models/user.model';
import { environment } from '../../../environments/environment';

/**
 * Authentication service handling login, register, logout and user state management
 * Keeps reactive current user state via BehaviorSubject, persists token & user into localStorage
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  /**
   * Internal subject holding reactive lightweight currentUser state, initialized from localStorage on service startup
   * Emits null when user is logged out or no valid persisted auth info exists.
   */
  private currentUserSubject = new BehaviorSubject<CurrentUser | null>(
    this.getUserFromStorage(),
  );
  /**
   * Public read-only observable stream for current user state, for components to subscribe via async pipe
   */
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private apiService: ApiService) {}

  /**
   * Register new user account
   * On successful registration, clear any existing local auth state, force user to login manually
   * Does NOT perform auto-login even if backend returns token
   *
   * @param data register payload contains username and password
   * @returns Observable of AuthResponse from backend
   */
  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.apiService.post<AuthResponse>('/auth/register', data).pipe(
      tap(() => {
        localStorage.removeItem(environment.jwtTokenKey);
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
      }),
    );
  }

  /**
   * User login, persist JWT token and lightweight CurrentUser into localStorage
   * Update BehaviorSubject with logged-in user when valid token received
   * Skip persistence if response does not contain valid token
   *
   * @param data login payload with username and password
   * @returns Observable of AuthResponse includes jwt token and username
   */
  login(data: LoginRequest): Observable<AuthResponse> {
    return this.apiService.post<AuthResponse>('/auth/login', data).pipe(
      tap((response) => {
        if (response.token) {
          localStorage.setItem(environment.jwtTokenKey, response.token);
          const user: CurrentUser = { username: response.username };
          localStorage.setItem('currentUser', JSON.stringify(user));
          this.currentUserSubject.next(user);
        }
      }),
    );
  }

  /**
   * Log out current user: clear localStorage token & user record, reset subject to null
   */
  logout(): void {
    localStorage.removeItem(environment.jwtTokenKey);
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  /**
   * Get raw JWT access token from localStorage
   *
   * @returns stored token string or null when missing
   */
  getToken(): string | null {
    return localStorage.getItem(environment.jwtTokenKey);
  }

  /**
   * Check if user is authenticated by existence of JWT token in local storage
   * This only checks client-side presence of token, does NOT validate token signature or expiry
   *
   * @returns true if token exists, false otherwise
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  /**
   * Get snapshot value of current lightweight user synchronously from subject
   * Prefer subscribing to currentUser$ observable inside components for reactive updates
   *
   * @returns CurrentUser object or null for logged-out state
   */
  getCurrentUser(): CurrentUser | null {
    return this.currentUserSubject.value;
  }

  /**
   * Get current username, return empty string if no user logged-in
   *
   * @returns username or empty string
   */
  getCurrentUsername(): string {
    const user = this.getCurrentUser();
    return user?.username || '';
  }

  /**
   * Reload auth state directly from localStorage and push new value into BehaviorSubject
   * Use after manual localStorage modification or page refresh recovery scenarios
   */
  public reloadUserFromStorage(): void {
    const user = this.getUserFromStorage();
    this.currentUserSubject.next(user);
  }

  /**
   * Private helper: parse persisted user object from localStorage
   * Catch corrupted JSON parsing error, clean up broken storage entries on failure
   *
   * @returns parsed CurrentUser or null
   */
  private getUserFromStorage(): CurrentUser | null {
    try {
      const user = localStorage.getItem('currentUser');
      return user ? JSON.parse(user) : null;
    } catch (error) {
      // Handle corrupted JSON in localStorage
      localStorage.removeItem('currentUser');
      localStorage.removeItem(environment.jwtTokenKey);
      return null;
    }
  }
}
