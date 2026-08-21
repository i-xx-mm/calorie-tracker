import { TestBed } from '@angular/core/testing';
import { FoodService } from './food.service';
import { ApiService } from './api.service';
import { of } from 'rxjs';
import { Food, FoodLog, DashboardData, MonthlyStatsResponse } from '../models/food.model';

describe('FoodService', () => {
  let service: FoodService;
  let apiService: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    const apiSpy = jasmine.createSpyObj('ApiService', ['get', 'post', 'put', 'delete']);

    TestBed.configureTestingModule({
      providers: [
        FoodService,
        { provide: ApiService, useValue: apiSpy }
      ]
    });
    service = TestBed.inject(FoodService);
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('searchFoods calls GET with correct query params', () => {
    const mockResult: Food[] = [];
    apiService.get.and.returnValue(of(mockResult));

    service.searchFoods('apple', 15).subscribe();

    expect(apiService.get).toHaveBeenCalledWith('/foods?search=apple&limit=15');
  });

  it('getFoodById calls GET with food id', () => {
    apiService.get.and.returnValue(of({} as Food));
    service.getFoodById('food‑001').subscribe();
    expect(apiService.get).toHaveBeenCalledWith('/foods/food‑001');
  });

  it('createFood calls POST with payload', () => {
    const input = { name: 'banana', calorie: 90 };
    apiService.post.and.returnValue(of({} as Food));
    service.createFood(input).subscribe();
    expect(apiService.post).toHaveBeenCalledWith('/foods', input);
  });

  it('updateFood calls PUT with id and partial food', () => {
    const updatePayload = { calorie: 95 };
    apiService.put.and.returnValue(of({} as Food));
    service.updateFood('food‑001', updatePayload).subscribe();
    expect(apiService.put).toHaveBeenCalledWith('/foods/food‑001', updatePayload);
  });

  it('deleteFood calls DELETE on foods/:id', () => {
    apiService.delete.and.returnValue(of(void 0));
    service.deleteFood('food‑001').subscribe();
    expect(apiService.delete).toHaveBeenCalledWith('/foods/food‑001');
  });

  it('getFoodLogByDate appends date query param', () => {
    apiService.get.and.returnValue(of({} as FoodLog));
    service.getFoodLogByDate('2026‑08‑21').subscribe();
    expect(apiService.get).toHaveBeenCalledWith('/foodlogs?date=2026‑08‑21');
  });

  it('createFoodLog POST foodlog payload', () => {
    const logPayload: Omit<FoodLog, 'id'> = {
      date: '2026‑08‑21',
      username: 'testuser',
      foods: [],
      totalCalories: 0
    };
    apiService.post.and.returnValue(of({} as FoodLog));
    service.createFoodLog(logPayload).subscribe();
    expect(apiService.post).toHaveBeenCalledWith('/foodlogs', logPayload);
  });

  it('addFoodEntry builds payload, uses passed‑in date if provided', () => {
    apiService.post.and.returnValue(of({} as FoodLog));
    service.addFoodEntry('oatmeal', 220, 'breakfast note', '2026‑08‑20').subscribe();

    expect(apiService.post).toHaveBeenCalledWith('/foodlogs', {
      foodName: 'oatmeal',
      calorie: 220,
      note: 'breakfast note',
      date: '2026‑08‑20'
    });
  });

  it('updateFoodEntry calls PUT with logId and index query param', () => {
    const payload = { foodName: 'rice', calorie: 180, note: '' };
    apiService.put.and.returnValue(of({} as FoodLog));
    service.updateFoodEntry('log‑123', 2, 'rice', 180).subscribe();
    expect(apiService.put).toHaveBeenCalledWith('/foodlogs/log‑123?index=2', payload);
  });

  it('deleteFoodEntry calls DELETE with logId and index', () => {
    apiService.delete.and.returnValue(of({} as FoodLog));
    service.deleteFoodEntry('log‑123', 0).subscribe();
    expect(apiService.delete).toHaveBeenCalledWith('/foodlogs/log‑123?index=0');
  });

  it('getTodayDashboard GET /dashboard/today', () => {
    apiService.get.and.returnValue(of({} as DashboardData));
    service.getTodayDashboard().subscribe();
    expect(apiService.get).toHaveBeenCalledWith('/dashboard/today');
  });

  it('getMonthlyStats appends months query parameter, default =1', () => {
    apiService.get.and.returnValue(of({} as MonthlyStatsResponse));
    service.getMonthlyStats().subscribe();
    expect(apiService.get).toHaveBeenCalledWith('/dashboard/monthly-stats?months=1');

    service.getMonthlyStats(3).subscribe();
    expect(apiService.get).toHaveBeenCalledWith('/dashboard/monthly-stats?months=3');
  });
});