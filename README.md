# Comparison of bytecode manipulation tools
Bytecode manipulation involves changing the contents of a class file during runtime. This allows a program to
add functionalities that are not covered by the
Java specifications but are completely legal at the bytecode level.
There are several tools available for bytecode manipulation, but no comprehensive comparison of these tools exists. In this thesis, a basis for comparison was developed and the most common tools were compared with the aim of identifying differences and providing a practical decision-making aid.

Using common transformations and analysis tasks, it was shown that ASM continues to offer the highest performance, while ByteBuddy and Javassist have a low barrier to entry due to their high level of abstraction. BCEL proves to be less efficient and outdated in its API design, while the Class File API presents itself as a forward-looking alternative with a modern, ergonomic approach and embedded JDK integration. Furthermore, benchmarks confirmed that the Class File API performed better than ASM in some scenarios.

In general, it was shown that no tool performed particularly well or poorly in the evaluation criteria. The decision for the right tool should be made in the context of abstraction, control, and future-proofing. It also became apparent that the selection depends heavily on the application context and that it can be useful to compare the tools in terms of implementation effort. None of the tools offers comprehensive verification options, which is currently a field of research.
