exp-168 (2022-02-18)
--------------------

Add methods to ``BoundArguments`` for converting arguments to ``bool``.


exp-167 (2022-02-18)
--------------------

Add methods to ``BoundArguments`` for checking arguments for their type and
returning the converted value.


exp-166 (2022-02-08)
--------------------

Updated HTML produced by ``Utils.getExceptionChainAsHTML()`` for better
compatibility with WAF stack traces.


exp-165 (2022-02-08)
--------------------

Fixed HTML for UL4 stacktrace produced by ``Utils.getExceptionChainAsHTML()``.

Enhance CSS markup.


exp-164 (2021-12-01)
--------------------

Fixed UL4ON serialization of ``*`` and ``**`` arguments in template signatures.


exp-163 (2021-11-16)
--------------------

Added Color methods ``withhue()`` and ``withsat()`` to the output of the
``dir()`` function.


exp-162 (2021-11-16)
--------------------

Added the missing Color methods ``withhue()`` and ``withsat()``.


exp-161 (2021-11-16)
--------------------

Added the missing ``dict`` method ``keys()``.


exp-160 (2021-11-02)
--------------------

The UL4 function ``urlquote()`` now will encode the space character as ``%20``
instead of ``+``.


exp-159 (2021-08-04)
--------------------

Added support for the ``<?ignore?>``/``<?end ignore?>`` tag.


exp-158 (2021-07-23)
--------------------

It is now possible to implement method calls without having to create bound
method objects. (Of course when the method is fetched without being called
directly, a bound method object is still required.)

For classes that implement ``UL4GetAttr`` this can be done by overwriting
the default implementation of ``UL4GetAttr.callAttrUL4()``. The default
implementation calls ``UL4GetAttr.getAttrUL4()`` and then calls
``CallAST.call()`` for the result.

For classes that can't implement ``UL4GetAttr`` this can be done by overwriting
``UL4Type.callAttr()`` in the type class for the class.

For examples how this is done see the class ``Color`` (for the ``UL4GetAttr``
based version), or ``Str`` (for the non-``UL4GetAttr`` based version).


exp-157 (2021-07-09)
--------------------

For all interfaces and abstract classes where there's a version with and one
without support for passing an ``EvaluationContext``, there is only one version
now, and this version does support passing an ``EvaluationContext``. (So the
implementation is the one from ``UL4FooWithContext``, but the name is ``UL4Foo``).

This affects the following interfaces: ``UL4RenderWithContext``, ``UL4SetItem``,
``UL4GetItem``, ``UL4Len``, ``UL4SetAttr``, ``UL4Bool``, ``UL4Dir``,
``UL4GetAttr``, ``UL4Call``, ``UL4Abs``, ``Function``, ``FunctionWithContext``,
``BoundMethod``, ``BoundMethodWithContext`` and ``UL4SetItemWithContext``.

Support for constant folding has been dropped.

``UndefinedKey`` objects now remember which object created them and they will
only be used for item access. Attribute access uses the new
``UndefinedAttribute`` class.


exp-156 (2021-06-15)
--------------------

Added a ``date`` method to ``date`` and ``datetime`` objects.

Added ``Utils.toBigDecimal(int)`` and ``Utils.toBigDecimal(long)``.

Always raise an ``ArithmeticException`` when dividing by zero.

Implemented support for ``timedelta // timedelta``.

Added a convenience method ``UL4Repr.repr()``.

Added methods ``Connection.commit()`` and ``Connection.rollback()``.

``com.livinglogic.ul4.utils.formatMessage()`` now supports argument indexes in
the placeholder strings, i.e. you can use ``{0}`` instead of ``{}`` to output
the first argument. This makes it possible to output the argument in a different
order.

Updated Apache Commons Lang to 3.11 and use Apache Commons Text 1.9.

Added methods ``Color.fromCSS(String)`` and ``Color.fromCSS(String, Color)``.

Added type objects. ``type(obj)`` now returns a type object instead of a string.
Some type objects can be called to create an instance.

Add function ``isinstance()`` for instance checking against type objects.

The following builtins are type objects now: ``bool``, ``int``, ``float``,
``str``, ``date``, ``datetime``, ``timedelta``, ``monthdelta``, ``list``,
``set``, ``dict`` and ``color.Color`` (new).

Add support for UL4 modules. Add the modules ``ul4``, ``math``, ``operator``
and ``color`` (and ``ul4on``, which existed before). A module has attributes
``__name__`` and ``__doc__``as well as additional attributes.

The module ``ul4`` contains all the types required for UL4 syntax trees. The
type ``ul4.Template`` is callable to create a new UL4 template from source.

The module ``ul4on`` contains the functions ``loads()`` and ``dumps()`` and
the types ``Encoder`` and ``Decoder``.

The module ``operator`` contains one type: ``attrgetter``.

The module ``color`` contains the type ``Color`` and the functions ``css()``
and ``mix()``.

``math`` contains the constants ``e``, ``pi`` and ``tau`` as well as the
functions ``cos()``, ``sin()``, ``tan()``, ``sqrt()`` and ``isclose()``.

Add support for positional-only and keyword-only arguments in the ``Signature``
API (but not in UL4 itself, i.e. currently it's not possible to define local
templates with positional-only or keyword-only arguments).

Add functions ``floor()`` and ``ceil()``.

``Template`` objects no longer have ``startdelim`` and ``enddelim`` attributes.
The tag delimiters are now always ``<?`` and ``?>``.

The color method ``abslum()`` has been renamed to ``abslight()`` and
``rellum()`` has been renamed to ``rellight()``.

The following methods have been added to ``color.Color``: ``hue()``,
``light()``, ``sat()``, ``withhue()``, ``withsat()``, ``withlum()``,
``ablum()``, ``rellum()``, ``invert()`` and ``combine()``.


exp-155 (?)
-----------

Internal unpublished version.


exp-154 (2020-04-29)
--------------------

Exceptions can now be logged for all logging levels in ``EvaluationContext``
(i.e. an exception can be logged as a wrning). Logging exceptions per se is
gone now.


exp-153-1 (2020-04-20)
----------------------

Arrays are now dumped as lists by the UL4ON machinery (Note that this means that
they don't roundtrip, as they will be deserialized as lists again.)


exp-153 (2020-04-14)
--------------------

Implemented a "module" ``ul4on`` with the attributes ``loads``, ``dumps``,
``Encoder`` and ``Decoder``. ``Encoder`` and ``Decoder`` can be used to
dump/load multiple objects/dumps using the same encoding/decoding context.


exp-152-1 (2019-12-12)
----------------------

Implemented ``InterpretedTemplate.reader(globalVariables, variables)``.


exp-152 (2019-12-12)
--------------------

Add methods ``logDebug``, ``logInfo``, ``logNotice``, ``logWarning``,
``logError`` and ``logException`` to ``EvaluationContext``. With this UL4
clients can log deprecations. To really have an effect, a subclass of
``EvaluationContext`` must be used that overwrites these methods.

Added support for global variables.

The set of variants of the methods ``renders``, ``render`` and ``call`` in
``InterpretedTemplate`` has changed.


exp-151-1 (2019-11-21)
----------------------

Actually clip the stack trace in
``com.livinglogic.ul4.Utils.getStacktraceAsMarkdown()``.


exp-151 (2019-11-21)
--------------------

Added version of ``com.livinglogic.ul4.Utils.getStacktraceAsText()`` and
``com.livinglogic.ul4.Utils.getStacktraceAsMarkdown()`` where the number of
lines can be limited.


exp-150 (2019-11-21)
--------------------

Added ``com.livinglogic.ul4.Utils.getStacktraceAsText()`` and
``com.livinglogic.ul4.Utils.getStacktraceAsMarkdown()`` for returning a
complete Java stacktrace as a plain text or Markdown string.


exp-149-1 (2019-11-20)
----------------------

Fixed duplicate class name in
``com.livinglogic.ul4.Utils.getExceptionChainAsMarkdown()``.


exp-149 (2019-11-20)
--------------------

Added ``com.livinglogic.ul4.Utils.getExceptionChainAsMarkdown()`` which returns
an UL4 stacktrace in Markdown format.


exp-148 (2019-11-15)
--------------------

Added an (UL4 accessible) method ``queryone`` to
``com.livinglogic.dbutils.Connection``.


exp-147 (2019-11-14)
--------------------

``com.livinglogic.dbutils.Connection`` now implements ``AutoCloseable``.


exp-146 (2019-11-11)
--------------------

Removed the "type name" from ``nameUL4`` for all bound methods.

Implemented the dict method ``pop()``.

Implemented the function ``scrypt()``.


exp-145-1 (2019-08-27)
----------------------

Make the following methods in ``Utils`` public: ``getExceptionChainAsList()``,
``getExceptionChainAsJSON()``, ``getExceptionChainAsText()`` and
``getExceptionChainAsHTML()``.


exp-145 (2019-08-27)
--------------------

Implemented or exposed the following methods in the ``Utils`` class:
``exceptionAsMap()``, ``getExceptionChainAsList()``,
``getExceptionChainAsJSON()``, ``getExceptionChainAsText()`` and
``getExceptionChainAsHTML()``.


exp-144 (2019-08-27)
--------------------

Added the following methods to the class ``AST``:
``getTemplateDescriptionText()``, ``getTemplateDescriptionHTML()``,
``getLocationDescriptionText()``, ``getLocationDescriptionHTML()``,
``getSourceSnippetText()`` and ``getSourceSnippetHTML()``.


exp-143-1 (2019-08-05)
----------------------

Make the method ``AST.getPos()`` public.


exp-143 (2019-07-23)
--------------------

Fix test.


exp-142 (2019-07-23)
--------------------

Fixed version number in ``pom.xml``.


exp-141 (2019-07-23)
--------------------

Fixed a ``NullPointerException`` in ``InterpretedTemplate.Block.setIndent``.


exp-140 (2019-06-24)
--------------------

Added the following UL4 attributes to ``AST``: ``startpos``, ``startline``,
``stopcol``, ``startsource``, ``startsourceprefix`` and ``startsourcesuffix``.

Added the following UL4 attributes to block like AST nodes: ``stoppos``,
``stopline``, ``stopcol``, ``stopsource``, ``stopsourceprefix`` and
``stopsourcesuffix``.


exp-139 (2019-06-18)
--------------------

Expose the attribute ``items`` of ``com.livinglogic.ul4.SetAST`` to UL4.


exp-138 (2019-02-26)
--------------------

Added ``com.livinglogic.utils.ArrayUtils`` (with the method
``makeExtendedStringArray``).


exp-137 (2019-02-26)
--------------------

Added ``com.livinglogic.utils.ListUtils`` (with the method ``makeExtendedList``).


exp-136-3 (2019-01-08)
----------------------

Type names are now "interned" when writing an UL4ON dump.


exp-136-2 (2018-12-18)
----------------------

Fixed the UL4 attribute ``context`` for ``LocationException`` to use
``getInnerException`` instead of ``getCause``.


exp-136-1 (2018-12-18)
----------------------

Fixed exception decoration in ``CallRenderAST``: Even if the call is not from a
template, the decorating must be done when the innermost exception is not a
LocationException, because this is the original location of the error.


exp-136 (2018-11-29)
--------------------

If constant folding in the compiler fails, the compiler will now create an AST
node for the original operator (which means that the error will only surface
when the template gets executed, not when it gets compiled).

Exception chaining has been changed from chaining the exception object via
``initCause()`` to do it via ``addSuppressed()``. The reason is that there might
be exceptions that already have a cause, so calling ``initCause`` again will
fail. Using ``addSuppressed()`` should work in much more cases.


exp-135-3 (2018-11-14)
----------------------

Fix typo in ``InterpretedTemplate``.


exp-135-2 (2018-11-14)
----------------------

Add method ``InterpretedTemplate.getParentTemplate()``.


exp-135-1 (2018-11-14)
----------------------

Add method ``LocationException.getLocation()``.


exp-135 (2018-11-14)
--------------------

Move information required for printing UL4 exceptions into the base class
``AST``.


exp-134 (2018-11-08)
--------------------

The chaining of UL4 exceptions has been inverted. This means that the exception
that will get raised from the UL4 template is the original innermost exception.
``LocationException`` instances will be added as the "cause" of each exception
to specify the exact location in the UL4 source.

The structure of compiled UL4 templates has been simplified internally: Each
``AST`` instance has attributes ``template`` and ``pos`` that directly reference
the template and the source code location of the ``AST`` node. The ``Tag``
objects are gone (they will only be used internally during compilation).
Also ``AST`` nodes have gained a ``source`` property which returns the source
code of the node itself.


exp-133 (2018-11-02)
--------------------

Code in ``AttrAST`` and ``ItemAST`` has been reordered so that implementations
of the ``UL4GetAttrWithContext``, ``UL4GetAttr``, ``UL4GetItemWithContext`` and
``UL4GetItem`` interfaces are preferred over implementation of the ``Map``
interface.

For backwards compatibility reasons the UL4 function ``date`` now accepts
3-7 arguments again (and returns a ``datetime`` object when non-zero hours,
minutes, seconds or microsecond are passed in).


exp-132 (2018-09-14)
--------------------

Split date object into date and datetime objects and support ``LocalDate`` and
``LocalDatetime`` Java objects. Add ``today()`` function.


exp-131 (2018-02-07)
--------------------

Added support for loading the content of an object in an UL4ON dump
iteratively.


exp-130 (2018-01-10)
--------------------

Added support for ``<?renderblock?>`` and ``<?renderblocks?>``.


exp-129 (2017-12-20)
--------------------

Added support for ``<?renderx?>``.


exp-128 (2017-12-13)
--------------------

Fixed an off-by-one error in smart whitespace handling.


exp-127 (2017-11-17)
--------------------

The string methods ``startswith()`` and ``endswith()`` now support list of
strings as arguments.


exp-126 (2017-10-13)
--------------------

Use ``LinkedHashMap`` in ``CallAST`` and ``RenderAST`` to preserve the order
of keyword arguments.


exp-125 (2017-08-17)
--------------------

``Arrays.asList()`` returns immutables lists, and those don't support the
UL4 method ``pop()`` for example. Creating a new mutable list from the array
should fix that problem.


exp-124 (2017-08-03)
--------------------

To help will XSS prevention ``<`` will now be escaped as ``\u003c`` in JSON
output and as ``\x3c`` in UL4ON output.


exp-123 (2017-07-31)
--------------------

UL4 now longer tries a disguise objects as dictionaries. I.e. for objects
implementing ``UL4GetItemString`` the methods ``items()``, ``keys()``,
``values()`` and ``get()`` are no longer synthesized. This also means that
``len()``, ``list()``, item access and containment test no longer work on
objects.

New functions ``getattr()``, ``setattr()``, ``hasattr()`` and ``dir()`` have
been added, to work with attributes of objects.

A few interfaces (and their methods) have been renamed: ``UL4GetItemString``
to ``UL4GetAttr``, ``UL4GetItemStringWithContext`` to ``UL4GetAttrWithContext``
and ``UL4Attributes`` to ``UL4Dir``.


exp-122 (2017-04-18)
--------------------

The ``AttributeException`` constructor now has two arguments: The object and
the key.


exp-121 (2017-03-21)
--------------------

When deeserializing UL4ON dumps it's now possible to pass in a
"custom type registry" to customize which object get created.


exp-120 (2017-03-06)
--------------------

When compiling the template signature in an ``<?ul4?>`` tag fails the
exception will now be properly wrapped to show the ``<?ul4?>`` tag in which
the exception happened.


exp-119 (2017-03-03)
--------------------

Rendering or calling ``null`` now reliably produces a
``NotRenderableException``/``NotCallableException``.


exp-118 (2017-03-03)
--------------------

Fixed a minor bug when loading an UL4ON dump of a template in source form.


exp-117 (2017-02-28)
--------------------

Implementations of ``UL4GetItem``, ``UL4GetItemString``,
``UL4GetItemWithContext`` and ``UL4GetItemStringWithContext`` are now
required to throw an ``AttributeException`` when the attribute doesn't exist
(instead of returning an ``UndefinedKey`` object).

Added ``<?doc?>`` tag which is available as via the Java method
``InterpretedTemplate.getDoc()`` or the ``doc`` attribute in UL4.

The template signature is now accessible as the ``signature`` attribute
(and supports ``str()`` and ``repr()``).


exp-116 (2017-02-13)
--------------------

Fixed ``asjson()`` output of ``BigDecimal`` objects.


exp-115 (2017-02-13)
--------------------

UL4ON dumps can now contain UL4 templates in "source" format, i.e. the
template will be compiled when it is loaded. This is implemented to give the
Oracle PL/SQL version a chance to put UL4 templates into a UL4ON dump.

Compiling an UL4 template will wrap the ``RecognitionException`` in a
``RuntimeException``, so that the ``RecognitionException`` no longer has to
be declared (or wrapped) by calling code.


exp-114 (2017-02-12)
--------------------

Dictionaries generated by dictionary literals and dictionary comprehensions
are now ordered. Also the order of (key, value) pairs passed into an ``**``
parameter will now be preserved.


exp-113 (2017-01-26)
--------------------

Calling the ``InterpretedTemplate`` constructor with the ``Signature``
argument no longer overwrites a signature specified via an ``<?ul4?>`` tag.


exp-112 (2017-01-19)
--------------------

Fixed a off-by-one bug in ``Utils.unescapeUL4String()`` with ``\x`` and
``\u`` escapes.


exp-111 (2016-12-30)
--------------------

The UL4ON decoder now records the stream position, so it can be show in
exception messages.


exp-110 (2016-12-23)
--------------------

(Re)implemented the ``render`` method for templates (for backwards
compatibility). This method will go away again eventually.


exp-109 (2016-12-20)
--------------------

Enhanced error messages in the UL4ON decoder.


exp-108 (2016-12-16)
--------------------

UL4ON now supports ordered maps (typecode ``e``/``E``) for the Java type
``LinkedHashMap``.


exp-107 (2016-11-29)
--------------------

Optimize output of color alpha values in CSS format (limit to 3 decimal
places).


exp-106 (2016-11-28)
--------------------

Fixed alpha handling (``byte``/``double``) in ``Color.withlum()``.


exp-105 (2016-09-15)
--------------------

Added dictionary and set method ``clear``.

Added function ``md5``.

Expose attributes of ``DictItemAST``, ``ListAST``, ``SeqItemAST``,
``UnpackDictItemAST`` and ``UnpackSeqItemAST`` to UL4.


exp-104 (2016-05-17)
--------------------

Added support for exception objects (i.e. the function ``isexception`` and
exception attributes).

* Added the ``count`` method for strings and lists.

``istemplate()`` has been changed to return true if both of these interfaces
are implemented: ``UL4CallWithContext`` and ``UL4RenderWithContext``.


exp-103 (2016-05-04)
--------------------

Implemented changes from XIST 5.17: UL4 texts/tags now reference the template.


exp-102 (2016-03-18)
--------------------

``sorted()`` now supports the ``key`` and ``reverse`` arguments.

Sets now have a method ``add``.

Support for iterable unpacking in list and set literals has been added.

Support for dict/iterable unpacking in dict literals has been added.

Support for multiple uses of ``*`` and ``**`` arguments in calls has been added.

``repr()`` now produces the same output for strings as the Python version.

Support for the function ``ascii()`` has been added.

A string method ``splitlines()`` has been added.

Merged in the ``List``/``UL4Attributes`` fix from exp-99-8.


exp-101 (2016-03-02)
--------------------

``repr()`` will now fall back to return ``"<classname>"`` for unknown instances.

Added ``repr`` support for ``AST``, ``TextAST``, ``CodeAST`` and
``InterpretedTemplate``.


exp-100 (2015-12-02)
--------------------

Whitespace is now allowed before the tagname in UL4 tag, i.e. ``<? print 42 ?>``
will work.

Parsing json is now done with json-simple
(https://code.google.com/p/json-simple/)

Closures no longer see a frozen version of the variables at the time of the
``<?def?>`` tag, but the final state of the variables (like many other
programming languages do).

Updated for compatibility with XIST 5.14: smart whitespace handling and
related stuff has been implemented.


exp-99-8 (2016-03-23)
---------------------

Changed the order of tests in the implementation of ``len()`` so that
collections are checked first. This gives consistent results for ``len()``
and iterating an object if it implements both ``List`` and ``UL4Attributes``.


exp-99-7 (2015-09-28)
---------------------

Fixed a bug in the changes from exp-99-6.


exp-99-6 (2015-09-28)
---------------------

Added support for ``UL4GetItemString`` to ``ItemAST``.


exp-99-5 (2015-09-27)
---------------------

Added ``AbstractCombiningMapChain``.


exp-99-4 (2015-09-16)
---------------------

Fixed ``AttrAST``: The code path for ``UL4GetItemWithContext`` and
``UL4GetItemStringWithContext`` was wrong.


exp-99-3 (2015-09-16)
---------------------

The "combined" interfaces ``UL4GetAttributes``, ``UL4GetSetAttributes``,
``UL4GetSetItem`` and ``UL4GetSetItemString`` have been removed.

Two new interfaces ``UL4GetItemWithContext`` and ``UL4GetItemStringWithContext``
have been added. They allow to implement "dynamic attributes", i.e.
attributes whose values depend on the local variables. E.g. it would be
possible to implement an object ``double``, such that ``double.x`` returns
``2*x``.


exp-99-2 (2015-08-05)
---------------------

Fixed pom file from exp-99-1.


exp-99-1 (2015-08-05)
---------------------

Fixed handling of signatures when calling local templates.


exp-99 (2014-12-18)
-------------------

Calling ``Function`` and ``FunctionWithContent`` objects now destroys the
intermediate list objects that get created. This should help the Java GC
clean up unused objects.

A ``TemplateClosure`` no longer can reference itself via the variables from
its parents.

``UL4Repr.Formatter`` no longer calls the ``visit`` method in the constructor.
This makes it possible to subclass ``UL4Repr.Formatter`` for special output.
To use the ``Formatter`` use the following code::

	new UL4Repr.Formatter().visit(obj).toString()

An ``InterpretedTemplate`` can now have a signature. Calling or rendering the
template will now check the variables passed in against the signature. This
also works for subtemplates.

List slices now return new independent lists instead of views into the
original one.


exp-98 (2014-11-07)
-------------------

UL4ON now uses an ``IdentityHashMap`` for recording serialized objects. This
allows to serialize object loops.

Updated UL4ON to the more human readable version from XIST 5.12.


exp-97 (2014-10-29)
-------------------

Implement support for sets in UL4 and UL4ON.

Enhance ``FunctionRepr``: Now cycles will be detected automatically even for
classes that implement ``reprUL4()`` themselves.


exp-96-5 (2014-10-02)
---------------------

Only create an exception object in ``BoundDictMethodUpdate.call()`` when
necessary.


exp-96-4 (2014-10-01)
---------------------

Fixed typo in ``CLOBVar.fetch()``.


exp-96-3 (2014-10-01)
---------------------

Free ``CLOB``\s in ``CLOBVar.fetch()``.


exp-96-2 (2014-09-30)
---------------------

Free ``CLOB``\s in ``ResultSetMapIterator.fetch()``.


exp-96-1 (2014-09-29)
---------------------

Fixed the precedence of the boolean ``not`` operator: Now it has a lower
precedence than the comparison operators. i.e. ``not x in y`` is parsed
as ``not (x in y)``.


exp-96 (2014-09-25)
-------------------

``com.livinglogic.dbutils.Connection`` now has a new method ``execute()``
for executing database code that doesn't return a ``ResultSet``.

``com.livinglogic.dbutils.Connection`` has new methods ``int()``, ``number()``,
``str()``, ``clob()`` and ``date()`` that return variable objects that can be
used in ``query()``, ``queryargs()`` and ``execute()`` to receive out parameters.
The value returned is available in the ``value`` attribute.

``com.livinglogic.dbutils.Connection.queryargs()`` no longer supports keyword
arguments.

A problem with the evaluation order of arguments in calls has been fixed.


exp-95-2 (2014-08-07)
---------------------

Speed up ``FunctionAsJSON``: Instead of creating many temporary strings, the
code now formats the complete object into a ``StringBuilder``.


exp-95-1 (2014-08-05)
---------------------

Fixed UL4 implementation of ``queryargs()`` method in
``com.livinglogic.dbutils.Connection``.


exp-95 (2014-07-10)
-------------------

Fixed comparisons involving ``BigDecimal`` objects to ignore the scale.

Moved the code that registers the UL4 AST object for UL4ON into a static
method ``register4UL4ON()``.


exp-94-1 (2014-05-06)
---------------------

Added support for ``list(Iterable)``.


exp-94 (2014-05-05)
-------------------

Added ``while`` loop.

The maximum runtime of templates can now be limited by using an
``EvaluationContext`` object with a milliseconds value > 0.

Merged in exp-81-3 which fixes ``'``-escaping in JSON strings.


exp-93-1 (2014-01-23)
---------------------

Fixed version number.


exp-93 (2014-01-23)
-------------------

Slices are now handled by passing ``Slice`` objects as the index in ``Item``.


exp-92 (2014-01-10)
-------------------

The bitwise operators ``&``, ``|``, ``^``, ``~``, ``<<`` and ``>>``
(and their augmented assigment counterparts ``&=``, ``|=``, ``^=``, ``<<=`` and
``>>=``) have been added.

If expressions have been added.


exp-91 (2013-10-29)
-------------------

``com.livinglogic.dbutils.ResultSetMapIterator`` now returns the records as a
``org.apache.commons.collections.map.CaseInsensitiveMap``, i.e. keys are case
insensitive.


exp-90 (2013-10-01)
-------------------

Add support for attribute, item and slice assignment.


exp-89 (2013-08-28)
-------------------

Added bound methods. Instead of implementing ``UL4MethodCall``/``UL4MethodCallWithContext``,
simply return ``BoundMethod`` objects from ``getItemStringUL4()``.


exp-88 (2013-08-07)
-------------------

Added the UL4 functions ``first()`` and ``last()``.


exp-87 (2013-08-02)
-------------------

``Connection.query()`` has been renamed to ``Connection.queryargs()``.

``Connection.query()`` now requires at least one positional argument. Arguments
alternate between fragments of the SQL query and parameters that will be
embedded in the query.


exp-86 (2013-07-30)
-------------------

Make ``SetUtils`` methods generic.


exp-85 (2013-07-25)
-------------------

Add ``start`` argument to ``FunctionSum``.


exp-84 (2013-07-25)
-------------------

Add ``FunctionSum``.


exp-83 (2013-07-17)
-------------------

Add method ``SetUtils.makeExtendedSet()``.

Expose the text of ``Text`` nodes to templates.


exp-82 (2013-07-16)
-------------------

Object arrays are now supported everywhere ``List`` objects are.


exp-81-3 (2014-04-29)
---------------------

Fixed ``FunctionJSON.call()``: ``'`` may not be escaped in JSON strings
according to json.org (and jQuery).


exp-81-2 (2013-09-17)
---------------------

Add missing Javascript escape for JSON output of templates.


exp-81-1 (2013-09-17)
---------------------

Fixed JSON output of templates.


exp-81 (2013-07-03)
-------------------

Fixed a bug in ``CallMeth.evaluate()``, that surfaced when a ``*`` argument was
present.


exp-80 (2013-06-24)
-------------------

Added methods ``abslum()`` and ``rellum()`` to Color.


exp-79 (2013-06-17)
-------------------

Fixed a comparison bug in ``Utils.narrowBigInteger()``.


exp-78 (2013-06-17)
-------------------

``int(string)`` now returns a ``Long``/``BigInteger`` if the value overflows.

``int()`` and ``com.livinglogic.dbutils.Connection`` now try to convert
``BigInteger``\s to a narrower format (``Integer``/``Long``) if possible.


exp-77 (2013-06-14)
-------------------

Added support for positional parameters in ``com.livinglogic.dbutils.Connection``.


exp-76 (2013-06-13)
-------------------

Fixed strange ANTLR problems with triple quoted strings in various situations
(function calls etc.)


exp-75 (2013-06-13)
-------------------

Keys in database records are now converted to lower case.


exp-74 (2013-06-13)
-------------------

Added support for triple quoted strings.


exp-73 (2013-06-13)
-------------------

Exception chains for compiler error now have an additional stack level that
shows the tag the compile error happened in.


exp-72 (2013-06-13)
-------------------

Fixed a bug in the signature for ``Connection.query()``.


exp-71 (2013-06-06)
-------------------

``FunctionAsJSON`` now handles ``UL4Attributes`` objects.


exp-70 (2013-06-03)
-------------------

Implemented function ``slice()``.


exp-69 (2013-05-01)
-------------------

Added interface ``UL4Attributes`` that extends ``UL4GetItemString`` and allows
map style access to the attributes of an object.

Added interfaces ``UL4MethodCall`` and ``UL4MethodCallWithContext`` that allow
implementing arbitrary method calls.


exp-68 (2013-04-30)
-------------------

Renamed package ``com.livinglogic.oracleutils`` to ``com.livinglogic.dbutils``,
since it is no longer Oracle specific.


exp-67 (2013-04-30)
-------------------

Added function ``list()``.

Implemented support for custom methods via the interface ``UL4MethodCall`` and
``UL4MethodCallWithContext``.

Added support for resource cleanup in ``EvaluationContext``.

Added utilities for exposing database connections to UL4 templates.


exp-66 (2013-03-22)
-------------------

``removeWhitespace`` no longer removes the initial spaces in a string, but only
the whitespace *after* a linefeed.


exp-65 (2013-03-15)
-------------------

Moved ``removeWhitespace`` into ``InterpretedTemplate``, as it's only used there
to avoid package name conflicts.


exp-64 (2013-02-18)
-------------------

Implemented UL4 functions.

Removed builtin UL4 functions ``vars`` and ``get``.

Added methods ``append``, ``insert``, ``pop`` and ``update``.

Removed ``JavaSource4Template`` and ``JavascriptSource4Template`` (as this was
basically just a call to ``dumps()`` anyway).

Removed ``CompiledTemplate``.


exp-63 (2013-01-17)
-------------------

Removed ``ChainedHashMap``, as ``MapChain`` can be used instead now.

Removed ``EvaluationContext.keepWhitespace``, as this would be used for all
templates called, even if their value is different.

Formatting literal text is now done by the currently running template.


exp-62 (2013-01-14)
-------------------

Added support for the ``whitespace`` flag.


exp-61 (2013-01-10)
-------------------

Added support classes ``AbstractMapChain`` and ``MapChain``.

Added support for the automatic variable stack.

Added support for nested scopes/closures.

Added support for calling functions with a mixture of positional and keyword
arguments.


exp-60-1 (2012-12-07)
---------------------

Fixed ``FunctionBool`` for ``BigInteger`` and ``BigDecimal`` objects.


exp-60 (2012-11-15)
-------------------

To improve UL4 exception messages there are now several undefined objects,
which give information about which key/name/index resulted in the undefined
object being created.

AST nodes below the level of the tag now no longer have any location
information. This information is added when the exception bubbling reaches a
tag node.


exp-59 (2012-11-14)
-------------------

Added functions ``any()`` and ``all()``.


exp-58 (2012-11-12)
-------------------

``format()`` now works for integers.


exp-57 (2012-11-06)
-------------------

Use ``StringBuilder`` instead of ``StringBuffer`` everywhere.

``FunctionSort`` can now sort collections (lexicographically).

Added ``values`` method.


exp-56 (2012-11-01)
-------------------

Merged constant loading AST classes into one class: ``Const``.

UL4ON can now read/write ``TimeDelta`` and ``MonthDelta`` objects.

Added the ``Undefined`` singleton.

Implemented constant folding for binary and unary operators and ``GetSlice``.


exp-55 (2012-10-17)
-------------------

Added support for list/dict comprehension, generator expressions and the ``date``
function.

Added language argument to ``format`` function.

Added support for the ``week`` method.

Added support for ``timedelta`` and ``monthdelta`` objects.

Added support for the functions ``timedelta``, ``istimedelta``, ``monthdelta``
and ``ismonthdelta``.


exp-54 (2012-09-30)
-------------------

Variable unpacking is now supported for assignment too.


exp-53 (2012-09-28)
-------------------

Variable unpacking in for loops can now be nested arbitrarily deep.


exp-52 (2012-08-29)
-------------------

Fixed implementation of ``And`` to try the first operand first.


exp-51 (2012-08-08)
-------------------

Added the functions ``min()`` and ``max()``.

Added a proper (threaded) implementation of ``InterpretedTemplate.reader()``.


exp-50 (2012-07-17)
-------------------

The UL4 parser has been ported to ANTLR. The final jar doesn't
contain any Python/Jython any longer.

Moving to ANTLR made several syntax changes necessary:

*	``@2012-04-16`` becomes ``@(2012-04-16)``;

*	``<?render x()?>`` becomes ``<?print x.render()?>``;

*	``<?print x.render()?>`` becomes ``<?print x.renders()?>``.

UL4 templates now support the functions ``fromjson``, ``asul4on``, ``fromul4on``.

The function ``json`` has been renamed to ``asjson``.

Added support for templates and floats to UL4ON.


exp-49 (2012-03-13)
-------------------

Now the new style Javascript code generation is used (i.e. the code is
generated by Javascript itself).


exp-48 (2012-03-08)
-------------------

Renamed the function ``first``, ``last`` and ``firstlast`` to ``isfirst``,
``islast`` and ``isfirstlast``.


exp-47 (2012-03-07)
-------------------

Added support for the new UL4ON object serialization format (via the
class ``com.livinglogic.ul4on.Utils``).


exp-46 (2012-02-19)
-------------------

Added support for the new UL4 functions ``first()``, ``last()``, ``firstlast()``
and ``enumfl()``.


exp-45 (2011-09-07)
-------------------

Added new utility classes ``MapUtils``, ``ChainedHashMap`` and ``ObjectAsMap``.

``Template``, ``Opcode`` and ``Location`` now expose their attributes via a
``Map`` interface.


exp-44 (2011-09-07)
-------------------

Enhanced ``Location.toString()`` for literals.

Fixed ``TagException.toString()`` for parsing errors.


exp-43 (2011-07-22)
-------------------

Fixed location handling bugs with subtemplates.

Sub templates are now created by ``annotate()``.


exp-42 (2011-07-22)
-------------------

Updated to match the implementation in XIST 3.23 (i.e. names for templates).


exp-40 (2011-05-17)
-------------------

Added a new method ``Color.fromrepr()``.


exp-39 (2011-04-07)
-------------------

Fixed offsets into the source and the opcodes list for subtemplates.


exp-38 (2011-03-04)
-------------------

Updated Jython to version 2.5.2.


exp-37 (2011-02-24)
-------------------

Fixed comparison operator when only one of the arguments is ``null``.


exp-36 (2011-02-23)
-------------------

The functionality for generating Javscript source from a template has been
moved to a separate class ``JavascriptSource4Template``.

Fixed many bugs that were detected by running the XIST test suite with
templates converted to Java.

Updated ``commons-lang.jar`` to version 2.6 (``StringEscapeUtils.escapeJava()``
was escaping ``'/'`` in version 2.4).

``InterpretedTemplate`` now has a new method ``compileToJava()`` that can be
used to compile the template into native Java code. (This generates Java source
code for the template and compiles this with the help of the Java compiler).


exp-35 (2010-11-17)
-------------------

Sets can now be sorted.


exp-34 (2010-11-17)
-------------------

Iterators can now be sorted.


exp-33 (2010-11-09)
-------------------

Update file format to be compatible with XIST 3.15.


exp-32 (2010-11-08)
-------------------

Added ``InterpretedTemplate.reader()`` that returns a ``java.io.Reader`` object
for reading the template output.

Removed all versions of the ``render`` methods that didn't have a variables
argument.


exp-31 (2010-11-08)
-------------------

Added missing implementation for the ``contains`` opcode in
``InterpretedTemplate.Renderer()``.


exp-30 (2010-11-08)
-------------------

Added two methods ``InterpretedTemplate.render()`` that render the template
output to a ``java.io.Writer``.


exp-29 (2010-11-08)
-------------------

Fixed ``InterpretedTemplate.load()`` to conform to the format produced by Pythons
version.


exp-28 (2010-11-05)
-------------------

Fixed problems with linefeeds in comments for tag code in
``Template.javascriptSource()``.


exp-27 (2010-11-05)
-------------------

Added a new method ``InterpretedTemplate.javascriptSource()`` that generates
Javascript source from the template.

Updated date literals to used a ``@`` suffix.

Fixed various bugs.


exp-26 (2010-10-04)
-------------------

Support for the UL4 methods ``startswith`` and ``endswith`` has been added.


exp-25 (2010-09-17)
-------------------

Support for the UL4 function ``randchoice`` has been added.


exp-24 (2010-09-16)
-------------------

Support for the following new date methods has been added: ``day``, ``month``,
``year``, ``hour``, ``minute``, ``second``, ``microsecond``, ``weekday`` and
``yearday``.

Date parsing has been enhanced (microseconds are still not supported).

Support for the UL4 functions ``random`` and ``randrange`` has been added.


exp-23 (2010-02-25)
-------------------

Add support for more number types in the 1 and 2 arg version of ``toInteger()``.

Add support for more number types to ``toFloat()``.

Add support for more number types to ``repr()``.

``repr()`` of ``BigInteger``\s now ensures that the result contains a decimal
point.

Add support for more number types and ``Color`` objects to ``json()``.

Add support for more number types to ``chr()``.

Add support for more number types to ``hex()``/``oct()``/``bin()`` and fixed
the result for negative values.

Added the UL4 function ``utcnow()`` and the support method ``Utils.utcnow()``.

Added the UL4 method ``mimeformat()`` and the support method ``Utils.mimeformat()``.

The JSP render method has a ``Writer`` as argument instead of a ``JSPWriter``.


exp-22 (2010-02-08)
-------------------

The build file now forces compilation with Java 1.5.


exp-21 (2010-02-08)
-------------------

Now ``Utils.format()`` can be called without a locale argument (which is
useful for the JSP code generated by the XIST function
``ll.xist.ns.jsp.fromul4()``.


exp-20 (2010-01-14)
-------------------

``Utils.sub()``, ``Utils.mul()``, ``Utils.truediv()`` and ``Utils.floordiv()``
now support all valid combinations of bool/int/float/string operands.

``type()`` now returns the correct type for all ``Number`` subclasses.

Added function ``abs()``.


exp-19 (2009-12-07)
-------------------

``Utils.add()`` now supports all combinations of bool/int/float operands.


exp-18 (2009-11-17)
-------------------

``Utils.xmlescape()`` now uses ``ObjectUtils.toString()`` to support ``null``.


exp-17 (2009-11-16)
-------------------

Fixed error for unsupported operations.

``Utils.iterator()`` now supports ``Iterable`` not just ``Collection``.


exp-16 (2009-07-31)
-------------------

Fixed bug in the block nesting check logic.


exp-15 (2009-07-29)
-------------------

Reverted the fix to the ``rgb()`` function (arguments are float values between
0 and 1).


exp-14 (2009-07-29)
-------------------

Fixed the ``rgb()`` function.


exp-13 (2009-07-27)
-------------------

Updated to use Jython 2.5 (i.e. Java 1.5).

All that's needed to use Jython is now in ``ul4jython.jar`` (which is generated
by ``makejar.sh``).


exp-12 (2009-05-11)
-------------------

Added ``float()`` and ``iscolor()`` functions.


exp-11 (2009-03-07)
-------------------

Added ``join()`` method.


exp-10 (2009-02-28)
-------------------

Added ``reversed()`` function.


exp-9 (2009-02-16)
------------------

Added ``int()`` with two arguments.

Added ``render`` method.


exp-8 (2009-02-02)
------------------

Added support for ``Long`` in a few spots in ``Utils.java``.


exp-7 (2009-01-27)
------------------

Added interface ``JSPTemplate`` for an UL4 template converted to JSP.


exp-6 (2009-01-09)
------------------

Added ``<?note?>`` tag.

Added functions ``type()``, ``vars()``, ``zip()``.

Added one-arg ``find`` and ``rfind`` methods.

Added support for ``**`` in dict literals and render calls.

Added ``Template`` method ``pythonSource()``.

Added support for color objects.


exp-5 (2008-07-18)
------------------

Added ``printx`` tag/opcode.

Added string method ``capitalize()``.

Enhanced exceptions for unclosed blocks.

Added function ``get()`` and dictionary method ``get()``.

Fixed jump calculation for ``break``\s and ``continue``\s in ``for``-blocks.


exp-4 (2008-07-09)
------------------

Added support for a ``csvescape()`` function.


exp-3 (2008-07-09)
------------------

The ``org.apache.commons`` package is now used to implement some of the
operations.

Added support for a string method ``replace()``.

Added support for a ``repr()`` function.


exp-2 (2008-07-09)
------------------

Added ``break`` and ``continue`` tags/opcodes.


exp-1 (2008-07-09)
------------------

Initial version.
