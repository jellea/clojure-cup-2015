function oneLineCM(cm){
    cm.getWrapperElement().className += " monoline";
    cm.setSize("70%", cm.defaultTextHeight() + 2 * 4);
    cm.on("beforeChange", function(instance, change) {
        var newtext = change.text.join("").replace(/\n/g, ""); // remove ALL \n !
        change.update(change.from, change.to, [newtext]);
        return true;
    });
};
