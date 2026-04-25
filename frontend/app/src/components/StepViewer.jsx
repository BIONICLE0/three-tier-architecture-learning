import { useState } from 'react';

const ACTORS = ['browser', 'frontend', 'backend', 'cache', 'db'];

const actorLabel = {
  browser: 'ブラウザ',
  frontend: 'フロントエンド\n(React)',
  backend: 'バックエンド\n(Spring Boot)',
  cache: 'キャッシュ\n(Redis)',
  db: 'データベース\n(PostgreSQL)',
};

const actorColor = {
  browser:  { line: '#378ADD', bg: '#E6F1FB', text: '#0C447C' },
  frontend: { line: '#6366F1', bg: '#EEF2FF', text: '#3730A3' },
  backend:  { line: '#1D9E75', bg: '#E1F5EE', text: '#085041' },
  cache:    { line: '#E24B4A', bg: '#FCEBEB', text: '#791F1F' },
  db:       { line: '#BA7517', bg: '#FAEEDA', text: '#633806' },
};

const COL_WIDTH = 120;
const COL_START = 30;
const ROW_HEIGHT = 64;
const HEADER_H = 56;
const SELF_W = 36;

function getX(actor) {
  const idx = ACTORS.indexOf(actor);
  if (idx === -1) return COL_START; // fallback
  return COL_START + idx * COL_WIDTH + COL_WIDTH / 2;
}

export default function StepViewer({ steps }) {
  const [showDetail, setShowDetail] = useState(false);

  if (!steps || steps.length === 0) return (
    <p className="text-sm text-gray-400">操作すると処理の流れが表示されます</p>
  );

  const rowH = showDetail ? ROW_HEIGHT + 16 : ROW_HEIGHT;
  const svgH = HEADER_H + steps.length * rowH + 40;
  const svgW = COL_START + ACTORS.length * COL_WIDTH + 20;

  return (
    <div>
      <button
        onClick={() => setShowDetail(!showDetail)}
        className="mb-3 text-sm px-3 py-1 rounded bg-gray-200 hover:bg-gray-300"
      >
        {showDetail ? 'シンプル表示' : '詳細表示'}
      </button>

      <div className="overflow-x-auto">
        <svg width={svgW} height={svgH}>
          <defs>
            <marker id="arrowR" viewBox="0 0 10 10" refX="8" refY="5"
              markerWidth={6} markerHeight={6} orient="auto">
              <path d="M2 1L8 5L2 9" fill="none" stroke="context-stroke"
                strokeWidth={1.5} strokeLinecap="round" strokeLinejoin="round"/>
            </marker>
            <marker id="arrowL" viewBox="0 0 10 10" refX="2" refY="5"
              markerWidth={6} markerHeight={6} orient="auto">
              <path d="M8 1L2 5L8 9" fill="none" stroke="context-stroke"
                strokeWidth={1.5} strokeLinecap="round" strokeLinejoin="round"/>
            </marker>
            <marker id="arrowSelf" viewBox="0 0 10 10" refX="2" refY="5"
              markerWidth={6} markerHeight={6} orient="auto">
              <path d="M8 1L2 5L8 9" fill="none" stroke="context-stroke"
                strokeWidth={1.5} strokeLinecap="round" strokeLinejoin="round"/>
            </marker>
          </defs>

          {/* ヘッダー */}
          {ACTORS.map(actor => {
            const x = getX(actor);
            const c = actorColor[actor];
            return (
              <g key={actor}>

                <rect x={x - 44} y={6} width={88} height={30} rx={6}
                  fill={c.bg} stroke={c.line} strokeWidth={1}/>

                <text
                  x={x}
                  y={18}
                  textAnchor="middle"
                  fontSize={11}
                  fontWeight={500}
                  fill={c.text}
                >
                  {(actorLabel[actor] ?? actor).split('\n').map((line, i) => (
                    <tspan key={i} x={x} dy={i === 0 ? 0 : 14}>
                      {line}
                    </tspan>
                  ))}
                </text>
              </g>
            );
          })}

          {/* ライフライン */}
          {ACTORS.map(actor => {
            const x = getX(actor);
            const c = actorColor[actor];
            return (
              <line key={actor}
                x1={x} y1={40} x2={x} y2={svgH - 20}
                stroke={c.line} strokeWidth={1}
                strokeDasharray="4 4" opacity={0.35}
              />
            );
          })}

          {/* ステップ */}
          {steps.map((step, idx) => {
            const y = HEADER_H + idx * rowH + rowH / 2;
            const fromX = getX(step.from);
            const toX = getX(step.to);
            const isSelf = step.from === step.to;
            const c = actorColor[step.from] ?? actorColor['backend'];

            return (
              <g key={step.step}>
                {/* ステップ番号 */}
                <text x={14} y={y + 4} textAnchor="middle"
                  fontSize={10} fill="#9CA3AF">
                  {step.step}
                </text>

                {isSelf ? (
                  <>
                    <path
                      d={`M${fromX} ${y - 10} Q${fromX + SELF_W} ${y - 10} ${fromX + SELF_W} ${y} Q${fromX + SELF_W} ${y + 10} ${fromX} ${y + 10}`}
                      fill="none" stroke={c.line} strokeWidth={1.5}
                      markerEnd="url(#arrowSelf)"
                    />
                    <text x={fromX + SELF_W + 6} y={y + 4}
                      fontSize={11} fill="#374151">
                      {step.message}
                    </text>
                    {showDetail && step.detail && (
                      <text x={fromX + SELF_W + 6} y={y + 18}
                        fontSize={10} fill="#9CA3AF" fontFamily="monospace">
                        {step.detail}
                      </text>
                    )}
                  </>
                ) : (
                  <>
                    <line
                      x1={fromX < toX ? fromX + 2 : fromX - 2}
                      y1={y}
                      x2={fromX < toX ? toX - 8 : toX + 8}
                      y2={y}
                      stroke={c.line} strokeWidth={1.5}
                      markerEnd={fromX < toX ? 'url(#arrowR)' : 'url(#arrowL)'}
                    />
                    <text
                      x={(fromX + toX) / 2}
                      y={y - 7}
                      textAnchor="middle"
                      fontSize={11} fill="#374151">
                      {step.message}
                    </text>
                    {showDetail && step.detail && (
                      <text
                        x={(fromX + toX) / 2}
                        y={y + 14}
                        textAnchor="middle"
                        fontSize={10} fill="#9CA3AF" fontFamily="monospace">
                        {step.detail}
                      </text>
                    )}
                  </>
                )}
              </g>
            );
          })}
        </svg>
      </div>
    </div>
  );
}