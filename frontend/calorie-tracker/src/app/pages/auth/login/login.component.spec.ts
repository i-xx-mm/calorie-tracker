import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../shared/services/auth.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let notificationService: jasmine.SpyObj<NotificationService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', [
      'login',
      'reloadUserFromStorage',
    ]);
    const notifySpy = jasmine.createSpyObj('NotificationService', [
      'success',
      'error',
    ]);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: NotificationService, useValue: notifySpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    notificationService = TestBed.inject(
      NotificationService,
    ) as jasmine.SpyObj<NotificationService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form should be invalid when empty', () => {
    expect(component.loginForm.valid).toBeFalse();
  });

  it('username field validation: required and minLength 3', () => {
    const username = component.loginForm.controls['username'];
    username.setValue('');
    expect(username.hasError('required')).toBeTrue();

    username.setValue('ab');
    expect(username.hasError('minlength')).toBeTrue();

    username.setValue('myuser');
    expect(username.valid).toBeTrue();
  });

  it('password field validation: required and minLength 6', () => {
    const password = component.loginForm.controls['password'];
    password.setValue('');
    expect(password.hasError('required')).toBeTrue();

    password.setValue('12345');
    expect(password.hasError('minlength')).toBeTrue();

    password.setValue('123456');
    expect(password.valid).toBeTrue();
  });

  it('should call authService.login on valid form submit', () => {
    authService.login.and.returnValue(
      of({
        token: 'fake-jwt',
        username: 'testuser',
        expiresIn: 3600,
      }),
    );

    component.loginForm.setValue({
      username: 'testuser',
      password: '123456',
    });
    component.onSubmit();

    expect(authService.login).toHaveBeenCalledWith({
      username: 'testuser',
      password: '123456',
    });
  });

  it('should NOT call login if form invalid', () => {
    component.loginForm.setValue({ username: '', password: '' });
    component.onSubmit();
    expect(authService.login).not.toHaveBeenCalled();
  });
});
