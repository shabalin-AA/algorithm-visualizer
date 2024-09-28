import React, {useRef, useEffect} from 'react';

function lineRect(ctx: CanvasRenderingContext2D, x: number, y: number, w: number, h: number) {
  ctx.strokeRect(x, y, w, h);
}

function App() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  useEffect(() => {
    const canvas = canvasRef.current;
    if (canvas == null) { return }
    const ctx: CanvasRenderingContext2D | null = canvas.getContext('2d');
    if (ctx == null) { return }
    ctx.fillStyle = 'blue';
    ctx.lineWidth = 2;
    lineRect(ctx, 100, 100, 50, 50);
  }, []);

  return (
    <canvas 
      id="block-diagram" 
      width={800} 
      height={600} 
      ref={canvasRef} 
    />
  );
}

export default App;
