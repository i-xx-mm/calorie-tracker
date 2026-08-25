import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Food, FoodLog, DashboardData, MonthlyStatsResponse } from '../models/food.model';

/**
 * Food service encapsulates all Food-related backend API calls
 * Handles food database search, daily food log CRUD, and dashboard statistics endpoints
 * Provides helper to generate EST timezone date string for log operations
 */
@Injectable({
  providedIn: 'root'
})
export class FoodService {
  constructor(private apiService: ApiService) { }

  /**
   * Search food items from Food database
   * 
   * @param search search keyword string
   * @param limit max result count, default 10
   * @returns Observable array of matched Food records
   */
  searchFoods(search: string, limit: number = 10): Observable<Food[]> {
    return this.apiService.get<Food[]>(`/foods?search=${search}&limit=${limit}`);
  }

  /**
   * Fetch single day food log by target date string(YYYY-MM-DD)
   * 
   * @param date target date in YYYY-MM-DD format
   * @returns Observable FoodLog for specified date
   */
  getFoodLogByDate(date: string): Observable<FoodLog> {
    return this.apiService.get<FoodLog>(`/foodlogs?date=${date}`);
  }

  /**
   * Create brand new daily food log document
   * 
   * @param foodLog FoodLog payload without id field
   * @returns Observable created FoodLog from backend
   */
  createFoodLog(foodLog: Omit<FoodLog, 'id'>): Observable<FoodLog> {
    return this.apiService.post<FoodLog>('/foodlogs', foodLog);
  }

  /**
   * Convenience method: add one single food entry to food log
   * If date omitted, use current EST date. Backend handles log creation or entry append
   * 
   * @param foodName name of food item
   * @param calorie calorie amount
   * @param note optional user note
   * @param date target log date YYYY-MM-DD, fallback to today EST
   * @returns Observable updated FoodLog after entry added
   */
  addFoodEntry(foodName: string, calorie: number, note?: string, date?: string): Observable<FoodLog> {
    const payload = {
      foodName: foodName,
      calorie,
      note: note || '',
      date: date || this.getTodayDateString()
    };
    return this.apiService.post<FoodLog>('/foodlogs', payload);
  }

  /**
   * Update an existing food entry inside a food log by array index
   * 
   * @param foodLogId id of target food log document
   * @param foodItemIndex zero-based index of FoodItem inside entries array
   * @param foodName updated food name
   * @param calorie updated calorie value
   * @param note optional updated note
   * @returns Observable updated FoodLog after modification
   */
  updateFoodEntry(foodLogId: string, foodItemIndex: number, foodName: string, calorie: number, note?: string): Observable<FoodLog> {
      const payload = {
        foodName: foodName,
        calorie,
        note: note || ''
      };
      return this.apiService.put<FoodLog>(`/foodlogs/${foodLogId}?index=${foodItemIndex}`, payload);
    }

  /**
   * Delete single food entry from log by entry array index
   * 
   * @param foodLogId target food log document id
   * @param foodItemIndex zero-based entry index to remove
   * @returns Observable updated FoodLog after entry deletion
   */
  deleteFoodEntry(foodLogId: string, foodItemIndex: number): Observable<FoodLog> {
    return this.apiService.delete<FoodLog>(`/foodlogs/${foodLogId}?index=${foodItemIndex}`);
  }

  /**
   * Get dashboard aggregated data for today(EST timezone)
   * Includes total calories, daily target and today food entries summary
   * 
   * @returns Observable DashboardData
   */
  getTodayDashboard(): Observable<DashboardData> {
    return this.apiService.get<DashboardData>('/dashboard/today');
  }

  /**
   * Get monthly aggregated calorie statistics
   * 
   * @param months number of past months to compute stats, default 1
   * @returns Observable MonthlyStatsResponse
   */
  getMonthlyStats(months: number = 1): Observable<MonthlyStatsResponse> {
    return this.apiService.get<MonthlyStatsResponse>(`/dashboard/monthly-stats?months=${months}`);
  }

  /**
   * Private helper: return today date string under EST(America/New_York) timezone
   * Format: YYYY-MM-DD, used for food log date field
   * 
   * @returns EST-based date string YYYY-MM-DD
   */
  private getTodayDateString(): string {
    const now = new Date();
    const estDate = new Date(now.toLocaleString('en-US', { timeZone: 'America/New_York' }));
    const year = estDate.getFullYear();
    const month = String(estDate.getMonth() + 1).padStart(2, '0');
    const day = String(estDate.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}