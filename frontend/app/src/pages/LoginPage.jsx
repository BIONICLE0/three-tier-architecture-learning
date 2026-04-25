import { useState } from 'react';
import { login } from '../api/auth';
import PasswordInput from '../components/PasswordInput';

export default function LoginPage({ onGoRegister, onLoginSuccess, onSteps }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [result, setResult] = useState(null);

  const handleSubmit = async () => {
    const data = await login(username, password);
    setResult(data);
    onSteps(data.steps);
  };

  return (
    <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-6">
      <h2 className="text-xl font-bold mb-4">ログイン</h2>

      <input
        className="w-full border rounded px-3 py-2 mb-3"
        placeholder="ユーザー名"
        value={username}
        onChange={e => setUsername(e.target.value)}
      />
      <PasswordInput
        value={password}
        onChange={e => setPassword(e.target.value)}
      />

      <button
        onClick={handleSubmit}
        className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
      >
        ログイン
      </button>

      {result && (
        <>
          <p className={`mt-3 text-sm ${result.success ? 'text-green-600' : 'text-red-600'}`}>
            {result.success ? 'ログイン成功！' : 'ログイン失敗'}
          </p>
          {result.success && (
            <button
              onClick={() => onLoginSuccess(username)}
              className="mt-2 w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
            >
              マイページへ
            </button>
          )}
          {result.errors?.map((e, i) => (
            <p key={i} className="text-red-500 text-sm">{e.message}</p>
          ))}
        </>
      )}

      <p className="mt-4 text-sm text-center">
        アカウントをお持ちでない方は
        <button onClick={onGoRegister} className="text-blue-500 ml-1">登録</button>
      </p>
    </div>
  );
}