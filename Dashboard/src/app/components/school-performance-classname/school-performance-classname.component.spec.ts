import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SchoolPerformanceClassnameComponent } from './school-performance-classname.component';

describe('SchoolPerformanceClassnameComponent', () => {
  let component: SchoolPerformanceClassnameComponent;
  let fixture: ComponentFixture<SchoolPerformanceClassnameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SchoolPerformanceClassnameComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SchoolPerformanceClassnameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
