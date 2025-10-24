import { TestBed } from '@angular/core/testing';

import { ReportAdmin } from './report-admin';

describe('ReportAdmin', () => {
  let service: ReportAdmin;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReportAdmin);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
