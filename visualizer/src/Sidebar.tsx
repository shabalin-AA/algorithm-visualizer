import React, { FC } from 'react';
import { useDnD } from "./DnDContext";

const DnDComponent: FC = () => {
  const [_, setType] = useDnD();

  const onDragStart = (event: React.DragEvent<HTMLDivElement>, nodeType: string): void => {
    setType(nodeType);
    event.dataTransfer.effectAllowed = 'move';
  };

  return (
    <body>
      <div className="sidebar"> Перетащите узлы, чтобы создать их на поле.
        <div></div> 
        <div className="dndnode" onDragStart={(event) => onDragStart(event, 'NodeCalc')} draggable>
          Узел расчета
        </div>  
          <div onDragStart={(event) => onDragStart(event, 'NodeIf')} draggable>
            <svg width="100" height="100" className="shape-svg">
              <g transform="translate(2, 2)">
                <path d="M0,48 L48,0 L96,48 L48,96 Z" fill="#ffffff" stroke-width="1" stroke="#000000" fill-opacity="0.8">
                </path>
              </g>
            </svg>
          </div>
      </div>
    </body>
  );
};

export default DnDComponent;
