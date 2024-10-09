import { Handle, Node, NodeResizer, Position, NodeProps} from "@xyflow/react";
import "./styles.css"

type Vlo = Node<{label: string}>;

function NodeCustomPoints ({data}: NodeProps<Vlo>) {
  return (
    <div>
        <Handle type="source" position={Position.Bottom} />
        <div style={{ padding: 10 }}>{data.label}</div>
        <Handle type="target" position={Position.Right} />
    </div>
  );
};

export default NodeCustomPoints;
