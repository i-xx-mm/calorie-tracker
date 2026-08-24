import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private apiService: ApiService) { }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.apiService.post<AuthResponse>('/auth/register', data).pipe(
      tap(response => {
        if (response.token) {
          // Don't automatically log in after registration
          // User should go to login page to log in
          localStorage.removeItem(environment.jwtTokenKey);
          localStorage.removeItem('currentUser');
          this.currentUserSubject.next(null);
        }
      })
    );
  }

  login(data: LoginRequest): Observable<AuthResponse> {
    return this.apiService.post<AuthResponse>('/auth/login', data);
  }

  logout(): void {
    localStorage.removeItem(environment.jwtTokenKey);
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(environment.jwtTokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getCurrentUsername(): string {
    const user = this.getCurrentUser();
    return user?.username || '';
  }

  public reloadUserFromStorage(): void {
    const user = this.getUserFromStorage();
    this.currentUserSubject.next(user);
  }

  private getUserFromStorage(): User | null {
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