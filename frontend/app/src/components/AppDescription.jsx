export default function AppDescription() {
  return (
    <>
    <div className="bg-white rounded-2xl shadow-sm p-6">
      <h1 className="text-xl font-bold mb-3">
        3層WEBアプリケーションの学習サイト
      </h1>

      <div className="text-sm text-gray-700 space-y-4 text-left">
        <section>
          <h3 className="font-semibold">概要</h3>
          <p>
            このサイトでは3層WEBアプリケーションの仕組みをアカウント登録やログインといった操作を通じて体験できます。
          </p>
        </section>      

        <section>
          <h3 className="font-semibold">使い方</h3>
          <ol className="list-decimal pl-5">
            <li>左下の「操作画面」でアカウント登録やログインなどの操作を行います。</li>
            <li>右下の「リクエストフロー」に、操作に応じたリクエストの流れ（フロントエンド → バックエンド → データベース など）が可視化されます。</li>
          </ol>
        </section>

        <section>
          <h3 className="font-semibold">サーバ構成</h3>

          <p className="text-sm text-gray-600 mb-3">
            フロントエンドはReact（Vite）、バックエンドはSpring Bootを利用しています。
            データはPostgreSQLに保存し、必要に応じてRedisでキャッシュを行います。
          </p>

          <br></br>

          <img
            src="/architecture.png"
            alt="システム構成図"
            className="w-full max-w-3xl mx-auto"
            />
        </section>

      </div>

    </div>
    </>
  );
}