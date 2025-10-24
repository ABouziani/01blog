import { TestBed } from '@angular/core/testing';

import { PostAdmin } from './post-admin';

describe('PostAdmin', () => {
  let service: PostAdmin;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PostAdmin);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
