import { Handle, Node, Position, NodeProps} from "@xyflow/react"
import "./styles.css"
import CustomHandle from "./CustomHandle"

type Text = Node<{label: string}>;

function CustomNode ({data}: NodeProps<Text>) {
  return (
    <div> 
        <div style={{
          width: "50px", 
          height: "50px",
          background: '#fff',
          border: '1px solid #777',
          transform: "rotate(45deg)",
          }}>
        </div>
        <input type="text" className="node-label" placeholder="a > b"/>
        <div>
            <CustomHandle type="source" id="No" position={Position.Right} style={{right: -9}} connectioncount={1}/>
            <CustomHandle type="source" id="Yes" position={Position.Left} style={{left: -9}} connectioncount={1}/>
            <CustomHandle type="target" position={Position.Top} style={{top: -9}} connectioncount={1}/>
        </div>   
      </div>
  );
};

export default CustomNode;
