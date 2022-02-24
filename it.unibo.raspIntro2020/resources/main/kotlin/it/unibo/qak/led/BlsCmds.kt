package it.unibo.qak.led

sealed class BlsCmds( val cmd: String, val id : String ="blsCmd"  ){
    override fun toString() : String{ return "$id(${cmd})" }

    class LedCmd( cmd : String ) : BlsCmds(cmd, id=LedCmd.id){
        companion object{ val id = "ledCmd" }
    }
    class ButtonCmd( cmd : String ) : BlsCmds( cmd, id=ButtonCmd.id){
        companion object{ val id = "buttonCmd" }
    }
    class ControlCmd(cmd : String):BlsCmds( cmd, id=ControlCmd.id){
        companion object{ val id = "controlCmd" }
    }
}