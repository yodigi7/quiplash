import { TestBed } from '@angular/core/testing';

import { RestProxyService } from './rest-proxy.service';

describe('RestProxyService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RestProxyService = TestBed.get(RestProxyService);
    expect(service).toBeTruthy();
  });
});
