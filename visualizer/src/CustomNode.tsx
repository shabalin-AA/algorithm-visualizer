import { Handle, Node, NodeProps, Position } from "@xyflow/react";
import "./styles.css"


type CalcBlockNode = Node<{label: string}>;

function CalcBlock ({data}: NodeProps<CalcBlockNode>) {
  return (
    <div>
      <Handle
        type="target"
        position={Position.Top}
      />
      {data?.label}
      <Handle
        type="source"
        position={Position.Bottom}
      />
    </div>
  );
};

export default CalcBlock;
