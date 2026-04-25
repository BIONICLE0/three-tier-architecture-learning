import { useState } from 'react';
import { logout, updateUsername, updatePassword, deleteAccount } from '../api/auth';
import PasswordInput from '../components/PasswordInput';

export default function MyPage({ username, onLogout, onSteps }) {
  const [newUsername, setNewUsername] = useState('');
  const [password, setPassword] = useState('');
  const [result, setResult] = useState(null);

  const handle = async (fn) => {
    const data = await fn();
    setResult(data);
    onSteps(data.steps);
  };

  const handleLogout = async () => {
    const data = await logout();
    setResult(data);
    onSteps(data.steps);
  };

  const handleDelete = async () => {
    const data = await deleteAccount();
    setResult(data);
    onSteps(data.steps);
  };

  return (
      <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-6">
      <h2 className="text-xl font-bold">マイページ</h2>
      <h3 className="font-semibold mb-2">ようこそ、{username} さん</h3>

      <div>
        <h3 className="font-semibold mb-2">ユーザー名変更</h3>
        <input
          className="w-full border rounded px-3 py-2 mb-2"
          placeholder="新しいユーザー名"
          value={newUsername}
          onChange={e => setNewUsername(e.target.value)}
        />
        <p className="text-xs text-gray-500 mb-3">
          英数字・アンダースコアのみ（4〜20文字）
        </p>
        <button
          onClick={() => handle(() => updateUsername(newUsername))}
          className="w-full bg-green-500 text-white py-2 rounded hover:bg-green-600"
        >
          ユーザー名変更
        </button>
      </div>

      <br></br>

      <div>
        <h3 className="font-semibold mb-2">パスワード変更</h3>
        <PasswordInput
          value={password}
          onChange={e => setPassword(e.target.value)}
          placeholder="新しいパスワード"
        />
        <p className="text-xs text-gray-500 mt-1 mb-3">
          大文字・小文字・数字・記号をそれぞれ1文字以上含む（8〜128文字）
        </p>
        <button
          onClick={() => handle(() => updatePassword(password))}
          className="w-full bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600"
        >
          パスワード変更
        </button>
      </div>

      <br></br>

      <div>
        <h3 className="font-semibold mb-2">ログアウト</h3>
        <button
          onClick={handleLogout}
          className="w-full bg-gray-500 text-white py-2 rounded hover:bg-gray-600"
        >
          ログアウト
        </button>
      </div>

      <br></br>

      <div>
        <h3 className="font-semibold mb-2">アカウント削除</h3>
        <button
          onClick={handleDelete}
          className="w-full bg-red-500 text-white py-2 rounded hover:bg-red-600"
        >
          アカウント削除
        </button>
      </div>

      {result && (
        <>
          <br></br>
          <p className={`text-sm ${result.success ? 'text-green-600' : 'text-red-600'}`}>
            {result.success ? '成功' : '失敗'}
          </p>
          {result.errors?.map((e, i) => (
            <p key={i} className="text-red-500 text-sm">{e.message}</p>
          ))}
          {result.success && (result.steps?.some(s => s.message.includes('ログアウト') || s.message.includes('削除'))) && (
            <button
              onClick={onLogout}
              className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
            >
              ログイン画面へ
            </button>
          )}
        </>
      )}
    </div>
  );
}