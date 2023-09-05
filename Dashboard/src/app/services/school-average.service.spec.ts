import { TestBed } from '@angular/core/testing';

import { SchoolAverageService } from './school-average.service';

describe('SchoolAverageService', () => {
  let service: SchoolAverageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SchoolAverageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
