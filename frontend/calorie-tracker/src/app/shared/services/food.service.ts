import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Food, FoodLog, FoodItem, DashboardData, MonthlyStatsResponse } from '../models/food.model';

@Injectable({
  providedIn: 'root'
})
export class FoodService {
  constructor(private apiService: ApiService) { }

  // Food Database Operations - search from custom backend database only
  searchFoods(search: string, limit: number = 10): Observable<Food[]> {
    return this.apiService.get<Food[]>(`/foods?search=${search}&limit=${limit}`);
  }

  // Food Log Operations
  getFoodLogByDate(date: string): Observable<FoodLog> {
    return this.apiService.get<FoodLog>(`/foodlogs?date=${date}`);
  }

  createFoodLog(foodLog: Omit<FoodLog, 'id'>): Observable<FoodLog> {
    return this.apiService.post<FoodLog>('/foodlogs', foodLog);
  }

  addFoodEntry(foodName: string, calorie: number, note?: string, date?: string): Observable<FoodLog> {
    const payload = {
      foodName: foodName,
      calorie,
      note: note || '',
      date: date || this.getTodayDateString()
    };
    return this.apiService.post<FoodLog>('/foodlogs', payload);
  }

  updateFoodEntry(foodLogId: string, foodItemIndex: number, foodName: string, calorie: number, note?: string): Observable<FoodLog> {
      const payload = {
        foodName: foodName,
        calorie,
        note: note || ''
      };
      return this.apiService.put<FoodLog>(`/foodlogs/${foodLogId}?index=${foodItemIndex}`, payload);
    }

  deleteFoodEntry(foodLogId: string, foodItemIndex: number): Observable<FoodLog> {
    return this.apiService.delete<FoodLog>(`/foodlogs/${foodLogId}?index=${foodItemIndex}`);
  }

  // Dashboard Operations
  getTodayDashboard(): Observable<DashboardData> {
    return this.apiService.get<DashboardData>('/dashboard/today');
  }

  getMonthlyStats(months: number = 1): Observable<MonthlyStatsResponse> {
    return this.apiService.get<MonthlyStatsResponse>(`/dashboard/monthly-stats?months=${months}`);
  }

  private getTodayDateString(): string {
    // Get today's date in EST timezone (YYYY-MM-DD format)
    const now = new Date();
    const estDate = new Date(now.toLocaleString('en-US', { timeZone: 'America/New_York' }));
    const year = estDate.getFullYear();
    const month = String(estDate.getMonth() + 1).padStart(2, '0');
    const day = String(estDate.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}