import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentPerformanceStudentnameComponent } from './student-performance-studentname.component';

describe('StudentPerformanceStudentnameComponent', () => {
  let component: StudentPerformanceStudentnameComponent;
  let fixture: ComponentFixture<StudentPerformanceStudentnameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StudentPerformanceStudentnameComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentPerformanceStudentnameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
