import React, { FC } from 'react';
import { useDnD } from "./DnDContext";

const DnDComponent: FC = () => {
  const [_, setType] = useDnD();

  const onDragStart = (event: React.DragEvent<HTMLDivElement>, nodeType: string): void => {
    setType(nodeType);
    event.dataTransfer.effectAllowed = 'move';
  };

  return (
    <aside>
      <div className="description">Перетащите узлы, чтобы создать их на поле.</div>
      <div className="dndnode" onDragStart={(event) => onDragStart(event, 'NodeCalc')} draggable>
        Узел расчета
      </div>
      <div onDragStart={(event) => onDragStart(event, 'NodeIf')} draggable className='node-if'>
      <div style={{transform: "rotate(-45deg)"}}>Узел ветвления</div>
      </div>
    </aside>

  );
};

export default DnDComponent;
