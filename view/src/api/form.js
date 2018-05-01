const body = {
  host: 'lightapi.net',
  service: 'form',
  action: 'getFormById',
  version: '0.1.0',
  data: { formId: '00000162f3245333-0242ac1200070000' },
};

/**
 * Returns a promise from Form Service
 * @returns Promise
 */
export const getFormById = () => async () => {
  const res = await fetch('/api/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(body),
  });
  return res.json();
};
