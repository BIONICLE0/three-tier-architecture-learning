import { useState } from 'react';
import { register } from '../api/auth';
import PasswordInput from '../components/PasswordInput';

export default function RegisterPage({ onGoLogin, onSteps }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [result, setResult] = useState(null);

  const handleSubmit = async () => {
    const data = await register(username, password);
    setResult(data);
    onSteps(data.steps);
  };

  return (
    <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-6">
      <h2 className="text-xl font-bold mb-4">アカウント登録</h2>

      <input
        className="w-full border rounded px-3 py-2 mb-3"
        placeholder="ユーザー名"
        value={username}
        onChange={e => setUsername(e.target.value)}
      />

      <p className="text-xs text-gray-500 mb-3">
        英数字・アンダースコアのみ（4〜20文字）
      </p>

      <br></br>

      <PasswordInput
        value={password}
        onChange={e => setPassword(e.target.value)}
      />

      <p className="text-xs text-gray-500 mt-1 mb-3">
        大文字・小文字・数字・記号をそれぞれ1文字以上含む（8〜128文字）
      </p>

      <br></br>

      <button
        onClick={handleSubmit}
        className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
      >
        登録
      </button>

      {result && (
        <>
          <p className={`mt-3 text-sm ${result.success ? 'text-green-600' : 'text-red-600'}`}>
            {result.success ? '登録成功！' : '登録失敗'}
          </p>
          {result.success && (
            <button
              onClick={onGoLogin}
              className="mt-2 w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
            >
              ログインへ
            </button>
          )}
          {result.errors?.map((e, i) => (
            <p key={i} className="text-red-500 text-sm">{e.message}</p>
          ))}
        </>
      )}

      <p className="mt-4 text-sm text-center">
        すでにアカウントをお持ちの方は
        <button onClick={onGoLogin} className="text-blue-500 ml-1">ログイン</button>
      </p>
    </div>
  );
}