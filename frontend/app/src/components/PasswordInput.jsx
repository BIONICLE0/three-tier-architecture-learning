import { useState } from 'react';

export default function PasswordInput({ value, onChange, placeholder = 'パスワード' }) {
  const [show, setShow] = useState(false);

  return (
    <div className="relative mb-3">
      <input
        className="w-full border rounded px-3 py-2 pr-16"
        type={show ? 'text' : 'password'}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
      />
      <button
        type="button"
        onClick={() => setShow(!show)}
        className="absolute right-2 top-1/2 -translate-y-1/2 text-xs text-gray-500 hover:text-gray-700"
      >
        {show ? '隠す' : '表示'}
      </button>
    </div>
  );
}