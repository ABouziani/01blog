import { TestBed } from '@angular/core/testing';

import { UserAdmin } from './user-admin';

describe('UserAdmin', () => {
  let service: UserAdmin;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserAdmin);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
