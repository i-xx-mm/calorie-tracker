import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private apiService: ApiService) { }

  getCurrentUser(): Observable<User> {
    return this.apiService.get<User>('/users/me');
  }

  getProfile(username: string): Observable<User> {
    return this.apiService.get<User>(`/users/${username}`);
  }

  updateProfile(username: string, profile: Partial<User>): Observable<User> {
    return this.apiService.put<User>(`/users/${username}`, profile);
  }

  calculateBMI(heightCm: number, weightKg: number): number {
    const heightM = heightCm / 100;
    return Math.round((weightKg / (heightM * heightM)) * 10) / 10;
  }

  getBMICategory(bmi: number): string {
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Normal Weight';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  }

  getBMIStatus(bmi: number): string {
    if (bmi < 18.5 || bmi >= 30) return 'Unhealthy';
    return 'Healthy';
  }
}