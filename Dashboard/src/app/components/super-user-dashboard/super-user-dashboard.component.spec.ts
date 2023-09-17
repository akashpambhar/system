import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuperUserDashboardComponent } from './super-user-dashboard.component';

describe('SuperUserDashboardComponent', () => {
  let component: SuperUserDashboardComponent;
  let fixture: ComponentFixture<SuperUserDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SuperUserDashboardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SuperUserDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
