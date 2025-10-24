import { TestBed } from '@angular/core/testing';

import { ActiveTab } from './active-tab';

describe('ActiveTab', () => {
  let service: ActiveTab;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ActiveTab);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
