import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentPerformanceAllComponent } from './student-performance-all.component';

describe('StudentPerformanceAllComponent', () => {
  let component: StudentPerformanceAllComponent;
  let fixture: ComponentFixture<StudentPerformanceAllComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StudentPerformanceAllComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentPerformanceAllComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
