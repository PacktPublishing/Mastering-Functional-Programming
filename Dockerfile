FROM hseeberger/scala-sbt:8u222_1.3.3_2.13.1

RUN mkdir -p /root/.sbt/1.0/plugins
RUN echo "\
addSbtPlugin(\"io.get-coursier\" % \"sbt-coursier\" % \"1.0.0-RC12-1\")\n\
addSbtPlugin(\"io.spray\"        % \"sbt-revolver\" % \"0.9.0\"       )\n\
" > /root/.sbt/1.0/plugins/plugins.sbt

WORKDIR /root/examples
