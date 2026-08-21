import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../shared/services/user.service';
import { NotificationService } from '../../shared/services/notification.service';
import { User } from '../../shared/models/user.model';
import { AuthService } from '../../shared/services/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  currentUser: User | null = null;
  loading = true;
  saving = false;
  bmi: number | null = null;
  bmiStatus: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private notificationService: NotificationService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.initializeForm();
    this.loadUserProfile();
  }

  // Initialize form with default empty values
  initializeForm(): void {
    this.profileForm = this.formBuilder.group({
      username: [{ value: '', disabled: true }],
      height: ['', [Validators.required, Validators.min(50), Validators.max(300)]],
      weight: ['', [Validators.required, Validators.min(20), Validators.max(500)]],
      age: ['', [Validators.required, Validators.min(13), Validators.max(120)]],
      gender: ['', Validators.required]
    });
  }

  loadUserProfile(): void {
    this.loading = true;
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        if (!user) {
          this.notificationService.error('User data not available');
          this.loading = false;
          return;
        }
        this.currentUser = user;
        this.updateFormWithUser(user);
        this.calculateBMI();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading profile:', error);
        this.notificationService.error('Failed to load profile');
        this.loading = false;
      }
    });
  }

  // Update form with user data (after initialization)
  updateFormWithUser(user: User): void {
    this.profileForm.patchValue({
      username: user.username || '',
      height: user.height || '',
      weight: user.currentWeight || '',
      age: user.age || '',
      gender: user.gender || ''
    });
  }

  calculateBMI(): void {
    if (this.currentUser && this.currentUser.height && this.currentUser.currentWeight) {
      const heightInMeters = this.currentUser.height / 100;
      this.bmi = this.currentUser.currentWeight / (heightInMeters * heightInMeters);
      this.bmiStatus = this.getBMIStatus(this.bmi);
    }
  }

  getBMIStatus(bmi: number): string {
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Normal Weight';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  }

  getBMIStatusClass(status: string): string {
    switch (status) {
      case 'Underweight':
        return 'status-blue';
      case 'Normal Weight':
        return 'status-green';
      case 'Overweight':
        return 'status-orange';
      case 'Obese':
        return 'status-red';
      default:
        return '';
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.notificationService.error('Please fill in all required fields correctly');
      return;
    }

    this.saving = true;
    const payload = {
      height: this.profileForm.get('height')?.value,
      currentWeight: this.profileForm.get('weight')?.value,
      age: this.profileForm.get('age')?.value,
      gender: this.profileForm.get('gender')?.value
    };

    const username = this.authService.getCurrentUsername();
    this.userService.updateProfile(username, payload).subscribe({
      next: (user) => {
        this.currentUser = user;
        this.calculateBMI();
        this.notificationService.success('Profile updated successfully');
        this.saving = false;
      },
      error: (error) => {
        this.saving = false;
        console.error('Error updating profile:', error);
        this.notificationService.error(error.error?.message || 'Failed to update profile');
      }
    });
  }

  get f() {
    return this.profileForm.controls;
  }
}