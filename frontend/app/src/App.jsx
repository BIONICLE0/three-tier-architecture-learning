import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { me } from './api/auth';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MyPage from './pages/MyPage';
import StepViewer from './components/StepViewer';
import AppDescription from './components/AppDescription';

function AppRoutes({ onSteps }) {
  const [loggedIn, setLoggedIn] = useState(null);
  const [username, setUsername] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    me().then(data => {
      onSteps(data.steps);
      setLoggedIn(data.success);
      if (data.success) setUsername(data.username);
    });
  }, []);

  if (loggedIn === null) return <p className="text-center mt-20">読み込み中...</p>;

  const handleLoginSuccess = (username) => {
    setLoggedIn(true);
    setUsername(username);
    navigate('/mypage');
  };

  const handleLogout = () => {
    setLoggedIn(false);
    setUsername('');
    navigate('/');
  };

  return (
    <Routes>
      <Route
        path="/"
        element={loggedIn
          ? <Navigate to="/mypage" />
          : <LoginPage
              onGoRegister={() => navigate('/register')}
              onLoginSuccess={handleLoginSuccess}
              onSteps={onSteps}
            />}
      />
      <Route
        path="/register"
        element={loggedIn
          ? <Navigate to="/mypage" />
          : <RegisterPage
              onGoLogin={() => navigate('/')}
              onSteps={onSteps}
            />}
      />
      <Route
        path="/mypage"
        element={loggedIn
          ? <MyPage
              username={username}
              onLogout={handleLogout}
              onSteps={onSteps}
            />
          : <Navigate to="/" />}
      />
    </Routes>
  );
}

export default function App() {
  const [steps, setSteps] = useState([]);

  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-100">

        <div className="max-w-[1400px] mx-auto px-6 py-8">

          {/* 上：説明 */}
          <div className="mb-8">
            <AppDescription />
          </div>

          {/* 下：2:3レイアウト */}
          <div className="grid grid-cols-10 gap-6">

            {/* 左：操作（2） */}
            <div className="col-span-4 bg-white rounded-2xl shadow-sm p-6 flex flex-col">
              <h2 className="text-lg font-semibold mb-4">操作画面</h2>

              <div className="flex-1">
                <AppRoutes onSteps={setSteps} />
              </div>
            </div>

            {/* 右：フロー（3） */}
            <div className="col-span-6 bg-white rounded-2xl shadow-sm p-6 flex flex-col">
              <h2 className="text-lg font-semibold mb-4">リクエストフロー</h2>

              {/* 横スクロール許可 */}
              <div className="flex-1 overflow-auto">
                <StepViewer steps={steps} />
              </div>
            </div>

          </div>
        </div>
      </div>
    </BrowserRouter>
  );
}