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
        <div onDragStart={(event) => onDragStart(event, 'NodeCalc')} draggable>
          <div className='dndnode'>Узел расчета</div>
        </div>  
          <div onDragStart={(event) => onDragStart(event, 'NodeIf')} draggable>
            <div className='dndnode'>Узел ветвления</div>
          </div>
      </div>
    </body>
  );
};

export default DnDComponent;
