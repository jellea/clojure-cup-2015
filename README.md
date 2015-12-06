# Clojure Cup 2015

Submission by: Berlin Bang Bang

To support the amazing ClojureBridge initiative, we came up with the idea of an
interactive ClojureScript/Quil tutorial for beginners. As organisers of the
Berlin chapter of ClojureBridge, we think that this tutorial takes away some of
the initial hurdles, like editors, build tools and alien operating systems. It
needs some more love content-wise and we have some plans to make it even more
friendly, but technically it's already in a state where it can be used for our
next workshop in January.

Some of our features:

  * direct feedback/live coding in the browser
  * inline Quil documentation (hover over fns)
  * inline evaluation/results (Light Table like)
  * amazing error heads-up display
  * lazy loading of examples

Made with bootstrapped ClojureScript, CodeMirror, reagent and figwheel. No backend.

A next step would be to make it work really well on phone, as well as making a
gallery where users can post their sketches.

Takes inspiration from: Light Table, Bret Victor's talks, Quil examples on Quil.info

## Attribution

The tutorial content is based on the
[ClojureBridge curriculum](https://github.com/ClojureBridge/curriculum).
