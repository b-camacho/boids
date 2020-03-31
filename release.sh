sbt fullOptJS;
rm public/*;
cp static/* public/;
cp target/scala-2.12/boids-opt.js target/scala-2.12/boids-jsdeps.min.js public;
sed 's/\.\.\/target\/scala-2\.12\/boids-fastopt\.js/boids-opt.js/g' static/index.html > public/index.html;

