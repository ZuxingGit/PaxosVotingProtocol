# Location of trees.
SOURCE_DIR := .
OUTPUT_DIR := ./classes
# Java tools
JFLAGS := -sourcepath $(SOURCE_DIR)\
               -d $(OUTPUT_DIR)\
               -source 1.8
JAVADOC     := javadoc
JDFLAGS     := -sourcepath $(SOURCE_DIR)        \
               -d $(OUTPUT_DIR)                 \
               -link http://java.sun.com/products/jdk/1.8/docs/api            
 # Unix tools
 AWK         := awk
 FIND        := find
 MKDIR       := mkdir -p
 RM          := rm -rf
 SHELL       := /bin/bash
 
.PHONY: all
all: compile #TXT_TARGS

hello:
	@echo $(all_javas)

# all_javas - Temp file for holding source file list
all_javas := $(OUTPUT_DIR)/all.javas
# make-directories - Ensure output directory exists.
make-directories := $(shell $(MKDIR) $(OUTPUT_DIR))

# compile - Compile the source
.PHONY: compile
compile: $(all_javas)
	javac $(JFLAGS) @$<

# all_javas - Gather source file list
.INTERMEDIATE: $(all_javas)
$(all_javas):
	$(FIND) $(SOURCE_DIR) -name '*.java' > $@

# javadoc - Generate the Java doc from sources
.PHONY: javadoc
javadoc: $(all_javas)
	$(JAVADOC) $(JDFLAGS) @$<

#SRC_DIR := ./src/main/java/com/DS/server/content
#DEST_DIR := ./classes/com/DS/server/content
#FILES := source.txt
#
#TXT_TARGS : $(DEST_DIR)/$(FILES)
#
#$(DEST_DIR)/%.txt: $(SRC_DIR)/%.txt
#	cp -f $< $@

.PHONY: clean
clean:
	$(RM) $(OUTPUT_DIR)

proposer:
	@cd $(OUTPUT_DIR);\
	java members.Proposer

acceptor:
	@cd $(OUTPUT_DIR);\
	java members.Acceptor
	
m1:
	@cd $(OUTPUT_DIR);\
	java members.M1;
	
m2:
	@cd $(OUTPUT_DIR);\
	java members.M2
	
m3:
	@cd $(OUTPUT_DIR);\
	java members.M3
	
m4-9:
	@cd $(OUTPUT_DIR);\
	java members.M4_9

where:
	echo `pwd`