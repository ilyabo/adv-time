
PROF = dev
#PROF = prod
#PROF = dev,test,srcmap
# PROF = prod,test
# PROF = prod

CLIENT_PATH=adv-time-client
PREP_PATH=adv-time-prep

CLJSBUILD = client

all: autocompile

run: autocompile

openbrowser:
	(sleep 1 && open index.html) &

prep:
	@cd "$(PREP_PATH)" && \
	lein run

autocompile:
	@cd "$(CLIENT_PATH)" && \
	rm -rf target  && \
	lein with-profile $(PROF) cljsbuild auto $(CLJSBUILD)

prod: prep
	@cd "$(CLIENT_PATH)" && \
	rm -rf target && \
	lein with-profile prod cljsbuild once $(CLJSBUILD) && \
	cp target/$(CLJSBUILD).js ..

clean:
	@cd "$(CLIENT_PATH)" && \
	lein -o clean
