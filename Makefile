
PROF = dev
#PROF = prod
#PROF = dev,test,srcmap
# PROF = prod,test
# PROF = prod

CLIENT_PATH=adv-time-client

CLJSBUILD = client

all: autocompile

run: autocompile

openbrowser:
	(sleep 1 && open index.html) &

autocompile:
	@cd "$(CLIENT_PATH)" && \
	rm -rf target  && \
	lein with-profile $(PROF) cljsbuild auto $(CLJSBUILD)

prod:
	@cd "$(CLIENT_PATH)" && \
	rm -rf target && \
	lein with-profile prod cljsbuild once $(CLJSBUILD) && \
	cp target/$(CLJSBUILD).js ..

clean:
	@cd "$(CLIENT_PATH)" && \
	lein -o clean
