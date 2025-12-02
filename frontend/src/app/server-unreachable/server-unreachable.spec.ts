import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServerUnreachable } from './server-unreachable';

describe('ServerUnreachable', () => {
  let component: ServerUnreachable;
  let fixture: ComponentFixture<ServerUnreachable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServerUnreachable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServerUnreachable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
