define("libs/engine/misc.js", function(require, module, exports, process) {
 // cc.AuidioEngine
if (cc && cc.audioEngine) {
    cc.audioEngine._maxAudioInstance = 10;
}

// cc.Macro
cc.macro.DOWNLOAD_MAX_CONCURRENT = 10;
 
})